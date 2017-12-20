package co.com.sc.cert.server.controller;

import co.com.sc.cert.server.model.CertificateData;
import co.com.sc.cert.server.model.WSResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.BufferingContentSigner;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@RestController

public class GenerateCertificateController
{

    @RequestMapping(value = "/gen/cert", method = RequestMethod.POST)
    public WSResponse generateCertificate(@RequestBody CertificateData data) throws Exception
    {
        WSResponse response = new WSResponse();

        SecureRandom sr = new SecureRandom();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(1024, sr);
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privKey = keyPair.getPrivate();
        PublicKey pubKey = keyPair.getPublic();


        Date startDate = new Date();              // time from which certificate is valid
        Date expiryDate = DateUtils.addYears(startDate, 1);             // time after which certificate is not valid
        BigInteger serialNumber = new BigInteger("01");     // serial number for certificate
        X500Name dnName = new X500Name("CN=Test CA Certificate");

        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());

        X509v1CertificateBuilder certGen = new X509v1CertificateBuilder(dnName, serialNumber, startDate, expiryDate, dnName, subjectPublicKeyInfo);

        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256WithRSAEncryption");
        AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

        ContentSigner cs = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(PrivateKeyFactory.createKey(privKey.getEncoded()));

        X509CertificateHolder holder = certGen.build(cs);

        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(holder);






        response.setSuccessful(true);
        response.setMessage(Base64.getEncoder().encodeToString(privKey.getEncoded()));
        return response;

    }

}