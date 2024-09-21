package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UrlDto {
    @URL
    private String url;
}
