package co.com.sc.cert.server.controller;

import co.com.sc.cert.server.model.entities.Certificates;
import co.com.sc.cert.server.model.request.SignRequest;
import co.com.sc.cert.server.model.request.WSResponse;
import co.com.sc.cert.server.repository.CertificateRepository;
import co.com.sc.cert.server.service.AuthenticationService;
import co.com.sc.cert.server.util.S3Functions;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.Optional;

@RestController
public class SignController
{

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    CertificateRepository certificateRepository;

    @RequestMapping(value = "/sign", method = RequestMethod.POST)
    public WSResponse loginUser(@RequestBody SignRequest signRequest)
    {
        WSResponse response = authenticationService.authenticate(signRequest.getAuthentication());
        if (!response.isSuccessful())
            return response;

        try
        {
            if (signRequest.getIdCert() == null || signRequest.getCertFileName() == null || signRequest.getPassFileName() == null)
                throw new Exception("Petición con datos incompletos");

            Optional<Certificates> opt = certificateRepository.findById(signRequest.getIdCert());

            if(!opt.isPresent())
                throw new Exception("El certificado con el id " + signRequest.getIdCert() + " no existe.");

            byte[] cert = opt.get().getCert();
            byte[] pass = signRequest.getAuthentication().getPassword().getBytes(Charset.forName("UTF-8"));

            S3Functions.uploadPrivateFile(signRequest.getCertFileName(), cert);
            S3Functions.uploadPrivateFile(signRequest.getPassFileName(), pass);

            response.setMessage("Proceso de Firma terminado satisfactoriamente");
            response.setSuccessful(true);

        }catch(Exception e)
        {
            response.setMessage(e.getMessage());
            response.setSuccessful(false);
        }
        return response;

    }

}
