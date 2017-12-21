package co.com.sc.cert.server.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CertDetails
{
    Long id;
    String serialNumber;
    String dn;
    String issuer;
    String expireDate;
}
