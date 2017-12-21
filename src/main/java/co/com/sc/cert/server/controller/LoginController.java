package co.com.sc.cert.server.controller;

import co.com.sc.cert.server.model.WSResponse;
import co.com.sc.cert.server.model.request.Authentication;
import co.com.sc.cert.server.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class LoginController
{
    @Autowired
    AuthenticationService authenticationService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public WSResponse loginUser(@RequestBody  Authentication authentication)
    {
       return authenticationService.authenticate(authentication);
    }

}
