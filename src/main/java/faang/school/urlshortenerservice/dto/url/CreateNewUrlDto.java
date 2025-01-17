package faang.school.urlshortenerservice.dto.url;

import lombok.Data;

import java.net.URL;

@Data
public class CreateNewUrlDto {
    @org.hibernate.validator.constraints.URL
    private URL url;
}
