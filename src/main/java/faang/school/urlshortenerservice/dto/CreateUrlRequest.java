package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a short URL")
public class CreateUrlRequest {

    @NotBlank(message = "URL cannot be blank")
    @Schema(description = "Original URL to shorten", example = "https://www.example.com/very/long/url/path", required = true)
    private String url;
}

