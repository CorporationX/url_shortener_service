package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequest {
    @NotNull
    @URL
    private String url;
}
