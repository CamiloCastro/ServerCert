package co.com.sc.cert.server.repository;

import co.com.sc.cert.server.model.entities.Certificates;
import co.com.sc.cert.server.model.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificates, Long>
{

    Optional<Certificates> findById(Long id);

    List<Certificates> findAllByPerson(Person person);

}
