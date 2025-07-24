package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String hash;
    @URL(message = """
            Invalid URL, URL must start with http:// or https://,
             and must have be valid URL
            """)
    @NotNull(message = "URL must be not Null")
    @NotBlank(message = "Url can not be Empty")
    private String url;
    private LocalDateTime createdAt;
}
