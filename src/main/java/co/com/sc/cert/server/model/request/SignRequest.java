package co.com.sc.cert.server.model.request;

import lombok.Data;

@Data
public class SignRequest
{
    private Authentication authentication;
    private Long idCert;
    private String certFileName;
    private String passFileName;
}
