package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UrlDto {

    @URL
    @Size(max = 2048)
    private String url;
}
