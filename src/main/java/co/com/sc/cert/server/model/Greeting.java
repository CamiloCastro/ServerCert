package co.com.sc.cert.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Greeting
{
    private long id;
    private String content;
}
