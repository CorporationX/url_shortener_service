package faang.school.urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing the created short URL")
public class CreateUrlResponse {
    
    @Schema(description = "Shortened URL", example = "http://localhost:8080/abc123")
    private String shortUrl;
}

