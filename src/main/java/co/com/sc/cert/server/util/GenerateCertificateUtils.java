package co.com.sc.cert.server.util;

import co.com.sc.cert.server.model.request.Authentication;
import co.com.sc.cert.server.model.request.CertificateData;
import co.com.sc.cert.server.service.GenerateCertificateService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

public class GenerateCertificateUtils
{
    public byte[] generateCertificate(Authentication authentication, String dn) throws Exception
    {
        KeyPair kp = generateKeyPair();

        //Certifcate Subject
        X500Name subjectDN = new X500Name(dn);

        PrivateKey privateKey = kp.getPrivate();
        PublicKey publicKey = kp.getPublic();

        //Serial Number
        Random rnd = new Random();
        Integer i = rnd.nextInt();
        BigInteger serialNumber = new BigInteger(i.toString());     // serial number for certificate

        //Validity
        Date startDate = DateUtils.addSeconds(new Date(), -1);              // time from which certificate is valid
        Date expiryDate = DateUtils.addYears(startDate, 1);             // time after which certificate is not valid

        // SubjectPublicKeyInfo
        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());

        //Get CA Information
        CaCertificate caCertificate = getCaCertificate();
        X509Certificate caCert = caCertificate.getCertificate();
        PrivateKey caKey = caCertificate.getPrivateKey();

        //Issuer DN
        X500Name issuerName = new X500Name(caCert.getSubjectDN().getName());

        X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(issuerName,serialNumber, startDate, expiryDate, subjectDN,subjectPublicKeyInfo);

        DigestCalculator digCalc = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        X509ExtensionUtils x509ExtensionUtils = new X509ExtensionUtils(digCalc);

        // Subject Key Identifier
        certGen.addExtension(Extension.subjectKeyIdentifier, false,
                x509ExtensionUtils.createSubjectKeyIdentifier(subjectPublicKeyInfo));

        // Authority Key Identifier
        certGen.addExtension(Extension.authorityKeyIdentifier, false,
                x509ExtensionUtils.createAuthorityKeyIdentifier(subjectPublicKeyInfo));


        // Key Usage
        certGen.addExtension(Extension.keyUsage, false, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign
                | KeyUsage.cRLSign));

        // Extended Key Usage
        KeyPurposeId[] EKU = new KeyPurposeId[2];
        EKU[0] = KeyPurposeId.id_kp_emailProtection;
        EKU[1] = KeyPurposeId.id_kp_serverAuth;

        certGen.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(EKU));

        // Basic Constraints
        certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));

        ContentSigner cs = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(caKey);

        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certGen.build(cs));

        PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier)cert;

        bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("user_cert"));

        bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, new SubjectKeyIdentifier(publicKey.getEncoded()));

        X509Certificate[] chain = new X509Certificate[2];
        chain[1] = caCert;
        chain[0] = cert;

        return generateP12Bytes(chain, privateKey, authentication.getPassword());

    }

    private byte[] generateP12Bytes(X509Certificate[] chain, PrivateKey privateKey, String pass) throws Exception
    {

        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        store.load(null, null);

        store.setKeyEntry("user_key", privateKey, pass.toCharArray(), chain);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        store.store(baos, pass.toCharArray());
        baos.close();

        return baos.toByteArray();

    }


    private KeyPair generateKeyPair() throws Exception
    {
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048, sr);
        return keyGen.generateKeyPair();
    }

    private CaCertificate getCaCertificate() throws Exception
    {
        InputStream is = new ClassPathResource("CACert.p12").getInputStream();
        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
        store.load(is, "C0l0mbi41111".toCharArray());
        String alias = store.aliases().nextElement();
        PrivateKey key =  (PrivateKey) store.getKey(alias, "C0l0mbi41111".toCharArray());
        X509Certificate cert = (X509Certificate) store.getCertificate(alias);
        return new CaCertificate(cert, key);
    }

    @Data
    @AllArgsConstructor
    private class CaCertificate
    {
        X509Certificate certificate;
        PrivateKey privateKey;
    }
}
