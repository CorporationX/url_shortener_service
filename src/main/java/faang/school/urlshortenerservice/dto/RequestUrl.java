package faang.school.urlshortenerservice.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class RequestUrl {
    @URL
    private String url;
}
