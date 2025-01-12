package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @NotNull(message = "Url can't be null")
    @NotBlank(message = "Url can't be blank")
    @URL(message = "Invalid URL format")
    String url;
}
