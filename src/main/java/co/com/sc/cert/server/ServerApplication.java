package co.com.sc.cert.server;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.security.Security;

@SpringBootApplication
@EntityScan(basePackages= {"co.com.sc.cert.server.model.entities"})
public class ServerApplication
{
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        SpringApplication.run(ServerApplication.class, args);
    }
}
