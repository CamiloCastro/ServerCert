package co.com.sc.cert.server.repository;

import co.com.sc.cert.server.model.entities.Certificates;
import co.com.sc.cert.server.model.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificates, Long>
{

    List<Certificates> findAllByPerson(Person person);

}
