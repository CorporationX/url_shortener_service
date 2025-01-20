package faang.school.urlshortenerservice.model.dto;

import faang.school.urlshortenerservice.annotation.ValidUrl;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {
    @NotBlank(message = "URL must not be empty")
    @ValidUrl
    private String longUrl;
}
