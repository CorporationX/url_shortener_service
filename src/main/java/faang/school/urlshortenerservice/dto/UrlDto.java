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
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {
    private String hash;
    @URL(message = """
            Invalid URL, URL must look like https://yourwebsite.com
            """)
    @NotBlank(message = "Url can not be Empty")
    private String url;
    private LocalDateTime createdAt;
}