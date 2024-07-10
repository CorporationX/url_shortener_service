package faang.school.urlshortenerservice.dto;

import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private String url;
}