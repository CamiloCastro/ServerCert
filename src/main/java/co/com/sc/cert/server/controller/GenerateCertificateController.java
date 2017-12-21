package co.com.sc.cert.server.controller;

import co.com.sc.cert.server.model.request.Authentication;
import co.com.sc.cert.server.model.request.WSResponse;
import co.com.sc.cert.server.service.AuthenticationService;
import co.com.sc.cert.server.service.GenerateCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


//import java.security.cert.X509Certificate;


@RestController

public class GenerateCertificateController
{

    @Autowired
    GenerateCertificateService generateCertificateService;

    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = "/gen/cert", method = RequestMethod.POST)
    public WSResponse generateCertificate(@RequestBody Authentication authentication) throws Exception
    {
        WSResponse response = authenticationService.authenticate(authentication);
        if (!response.isSuccessful())
            return response;

        try
        {
            generateCertificateService.generateCertificate(authentication);
            response.setSuccessful(true);
            response.setMessage("Certificado generado exitosamente");
            return response;
        } catch (Exception e)
        {
            response.setSuccessful(false);
            response.setMessage(e.getMessage());
            return response;
        }

//        SecureRandom sr = new SecureRandom();
//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
//        keyGen.initialize(2048, sr);
//        KeyPair keyPair = keyGen.generateKeyPair();
//        PrivateKey privKey = keyPair.getPrivate();
//        PublicKey pubKey = keyPair.getPublic();
//
//
//        Date startDate = DateUtils.addSeconds(new Date(), -1);              // time from which certificate is valid
//        Date expiryDate = DateUtils.addYears(startDate, 10);             // time after which certificate is not valid
//        BigInteger serialNumber = new BigInteger("01");     // serial number for certificate
//        X500Name dnName = new X500Name("CN=Camilo's Root CA, O=Juan Camilo's Company, C=CO");
//
//
//        SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(pubKey.getEncoded());
//
//        X509v1CertificateBuilder certGen = new X509v1CertificateBuilder(dnName, serialNumber, startDate, expiryDate, dnName, subjectPublicKeyInfo);
//
//        ContentSigner cs = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider("BC").build(privKey);
//
//        X509CertificateHolder holder = certGen.build(cs);
//
//        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
//
//        cert.checkValidity(new Date());
//        cert.verify(pubKey);
//
//        PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier)cert;
//
//        bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_friendlyName, new DERBMPString("Camilo's Root CA"));
//
//        bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, new SubjectKeyIdentifier(pubKey.getEncoded()));
//
//        KeyStore store = KeyStore.getInstance("PKCS12", "BC");
//        store.load(null, null);
//
//        X509Certificate[] chain = new X509Certificate[1];
//        // first the client, then the CA certificate
//        chain[0] = cert;
//
//        store.setKeyEntry("CaKey", privKey, "C0l0mbi41111".toCharArray(), chain);
//
//        FileOutputStream fOut = new FileOutputStream("E:/Camilo/CACert.p12");
//        store.store(fOut, "C0l0mbi41111".toCharArray());
//        fOut.close();



    }

}
