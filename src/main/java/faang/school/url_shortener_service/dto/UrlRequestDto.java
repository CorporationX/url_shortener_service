package faang.school.url_shortener_service.dto;

import faang.school.url_shortener_service.config.swagger.annotations.ApiUrlRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    @ApiUrlRequestDto
    @NotBlank(message = "URL can't be empty or null")
    @URL(message = "Invalid URL format")
    private String originalUrl;
}