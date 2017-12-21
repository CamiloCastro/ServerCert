package co.com.sc.cert.server.model.request;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResultListCerts
{
    WSResponse wsResponse;

    @Getter
    List<CertDetails> certDetails = new ArrayList<>();
}
