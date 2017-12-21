package co.com.sc.cert.server.service;

import co.com.sc.cert.server.model.entities.Certificates;
import co.com.sc.cert.server.model.entities.Person;
import co.com.sc.cert.server.model.request.Authentication;
import co.com.sc.cert.server.model.request.CertificateData;
import co.com.sc.cert.server.repository.CertificateRepository;
import co.com.sc.cert.server.repository.PersonRepository;
import co.com.sc.cert.server.util.GenerateCertificateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.time.DateUtils;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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

@Service
public class GenerateCertificateService
{
    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    PersonRepository personRepository;

    public void generateCertificate(Authentication authentication) throws Exception
    {
        GenerateCertificateUtils utils = new GenerateCertificateUtils();
        Person p = personRepository.findOne(authentication.getUsername());

        byte[] p12Bytes = utils.generateCertificate(authentication,p.getDn());

        if(p12Bytes == null || p12Bytes.length == 0)
            throw new Exception("El certificado devuelto es nulo o está vacio");

        Certificates certificates = new Certificates();
        certificates.setCert(p12Bytes);
        certificates.setPerson(p);
        certificates = certificateRepository.save(certificates);

        if (certificates == null)
            throw  new Exception("Problemas al guardar el nuevo certificado");



    }


}
