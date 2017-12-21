package co.com.sc.cert.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages= {"co.com.sc.cert.server.model.entities"})
public class ServerApplication
{
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
