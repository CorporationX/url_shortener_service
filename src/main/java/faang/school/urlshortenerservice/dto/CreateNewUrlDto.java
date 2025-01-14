package faang.school.urlshortenerservice.dto;

import lombok.Data;

import java.net.URL;

@Data
public class CreateNewUrlDto {
    @org.hibernate.validator.constraints.URL
    private URL url;
}
