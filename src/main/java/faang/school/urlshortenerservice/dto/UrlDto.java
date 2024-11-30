package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
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
public class UrlDto {
    @URL(message = "Incorrect url!")
    @NotBlank(message = "Empty url!")
    private String url;
}
