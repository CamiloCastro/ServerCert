package co.com.sc.cert.server.controller;

import co.com.sc.cert.server.model.WSResponse;
import co.com.sc.cert.server.model.entities.Person;
import co.com.sc.cert.server.model.request.CertificateData;
import co.com.sc.cert.server.repository.PersonRepository;
import co.com.sc.cert.server.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class GenerateUserController
{

    @Autowired
    PersonRepository personRepository;

    @RequestMapping(value = "/gen/user", method = RequestMethod.POST)
    public WSResponse generateUser(@RequestBody CertificateData data)
    {
        WSResponse response = new WSResponse();
        try
        {
            Optional<Person> opt = personRepository.findByEmail(data.getAuthentication().getUsername());
            if (opt.isPresent())
                throw new Exception("El usuario ya existe");

            Person p = new Person();
            p.setDn(data.getDN());
            p.setEmail(data.getAuthentication().getUsername());
            p.setPassword(data.getAuthentication().getPassword());
            p = personRepository.save(p);
            if (p == null)
                throw new Exception("Error guardando el nuevo registro en la base de datos");
            response.setSuccessful(true);
            response.setMessage("Usuario registrado exitosamente");
        }catch (Exception e)
        {
            response.setSuccessful(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }


}
