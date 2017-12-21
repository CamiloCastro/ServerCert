package co.com.sc.cert.server.repository;

import co.com.sc.cert.server.model.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PersonRepository extends JpaRepository<Person, String>
{

    Optional<Person> findByEmail(String email);


}
