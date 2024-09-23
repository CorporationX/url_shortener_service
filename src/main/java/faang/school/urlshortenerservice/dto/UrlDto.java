package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    private String hash;

    @NotBlank(message = "Url can't be empty")
    @Pattern(regexp = "^(https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*$", message = "Incorrect url format")
    private String url;
    private LocalDateTime createdAt;
}
