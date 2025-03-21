package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @NotBlank(message = "Url can't be empty.")
    private String url;
}
