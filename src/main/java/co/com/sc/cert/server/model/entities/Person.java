package co.com.sc.cert.server.model.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Person
{
    @Id
    String email;
    String password;
    String dn;
}
