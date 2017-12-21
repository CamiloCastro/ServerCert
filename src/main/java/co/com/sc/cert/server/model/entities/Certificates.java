package co.com.sc.cert.server.model.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
public class Certificates
{
    @Id
    @GeneratedValue(generator = "SeqCertificates")
    @GenericGenerator(name = "SeqCertificates", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = { @Parameter(name = "sequence_name", value = "SEQ_CERTIFICATES")})
    private Long id;
    private byte[] cert;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID")
    private Person person;

}
