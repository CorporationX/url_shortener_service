package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {
    @NotNull(message = "Url can't be null")
    @NotBlank(message = "Url can't be blank")
    @URL(regexp = "(https?):\\/\\/)*")
    private String baseUrl;
}