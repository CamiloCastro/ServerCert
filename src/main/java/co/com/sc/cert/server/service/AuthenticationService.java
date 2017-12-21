package co.com.sc.cert.server.service;

import co.com.sc.cert.server.model.request.WSResponse;
import co.com.sc.cert.server.model.entities.Person;
import co.com.sc.cert.server.model.request.Authentication;
import co.com.sc.cert.server.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService
{

    @Autowired
    PersonRepository personRepository;

    public WSResponse authenticate(Authentication authentication)
    {
        WSResponse response = new WSResponse();

        try
        {
            if (authentication == null || authentication.getUsername() == null || authentication.getPassword() == null)
                throw  new Exception("Datos de acceso incompletos");

            Optional<Person> opt = personRepository.findByEmail(authentication.getUsername());
            if (!opt.isPresent())
                throw new Exception("El usuario no se encuentra registrado");

            Person p = opt.get();
            if (!p.getPassword().equals(authentication.getPassword()))
                throw new Exception("La contraseña no corresponde al usuario");

            response.setMessage("Autenticación exitosa");
            response.setSuccessful(true);

        }catch (Exception e)
        {
            response.setSuccessful(false);
            response.setMessage(e.getMessage());
        }
        return response;
    }

}
