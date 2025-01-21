package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {
    @NotBlank(message = "Url is empty")
    @URL(message = "Url is invalid")
    @Size(max = 2048, message = "Url must not exceed 2048 characters")
    private String url;
    private LocalDateTime expiresAt;
}