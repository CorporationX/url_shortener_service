package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlDto {
    @NotBlank(message = "URL cannot be blank")
    @Size(max = 2048, message = "URL cannot be longer than 2048 characters")
    @URL(message = "URL must be valid")
    private String url;
}
