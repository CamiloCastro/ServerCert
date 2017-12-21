package co.com.sc.cert.server.model.request;

import lombok.Data;

@Data
public class CertificateData
{
    Authentication authentication;
    String certName;
    String names;
    String lastNames;
    String email;
    String identification;
    String companyName;

    public String getDN()
    {
        return "CN=" + names + " " + lastNames + ",C=CO,L=Bogota,E=" + email + ",O=" + companyName + ",INITIALS=" + identification;
    }
}
