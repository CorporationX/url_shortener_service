package faang.school.urlshortenerservice.dto.url;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class SaveUrlDto {
    @URL
    private String url;
}
