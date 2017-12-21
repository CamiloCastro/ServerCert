package co.com.sc.cert.server.controller;

import co.com.sc.cert.server.model.WSResponse;
import co.com.sc.cert.server.model.entities.Certificates;
import co.com.sc.cert.server.model.entities.Person;
import co.com.sc.cert.server.model.request.Authentication;
import co.com.sc.cert.server.model.request.CertDetails;
import co.com.sc.cert.server.model.request.CertificateData;
import co.com.sc.cert.server.model.request.ResultListCerts;
import co.com.sc.cert.server.repository.CertificateRepository;
import co.com.sc.cert.server.repository.PersonRepository;
import co.com.sc.cert.server.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

@RestController
public class ListCertificatesController
{

    @Autowired
    CertificateRepository certificateRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = "/get/list", method = RequestMethod.POST)
    public ResultListCerts getCertsByPerson(@RequestBody Authentication authentication) throws Exception
    {
        ResultListCerts resultListCerts = new ResultListCerts();
        WSResponse response = authenticationService.authenticate(authentication);
        if (!response.isSuccessful())
        {
            resultListCerts.setWsResponse(response);
            return  resultListCerts;
        }

        try
        {
            Person p = personRepository.findOne(authentication.getUsername());
            List<Certificates> certificatesList = certificateRepository.findAllByPerson(p);
            for (Certificates certificates : certificatesList)
            {
                InputStream is = new ByteArrayInputStream(certificates.getCert());
                KeyStore store = KeyStore.getInstance("PKCS12", "BC");
                store.load(is, p.getPassword().toCharArray());
                String alias = store.aliases().nextElement();
                X509Certificate cert = (X509Certificate) store.getCertificate(alias);
                CertDetails cd = new CertDetails(certificates.getId(), cert.getSerialNumber().toString(), cert.getSubjectDN().toString(), cert.getIssuerDN().toString(), cert.getNotAfter().toString());
                resultListCerts.getCertDetails().add(cd);
            }
            response.setMessage("Datos enviados correctamente");
            response.setSuccessful(true);

        } catch (Exception e)
        {
            response.setMessage(e.getMessage());
            response.setSuccessful(false);
            resultListCerts.getCertDetails().clear();
        }

        resultListCerts.setWsResponse(response);
        return resultListCerts;


    }


}
