package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @NotNull(message = "URL cannot be null")
    @NotBlank(message = "URL cannot be blank")
    @URL(message = "Invalid URL")
    private String url;
}