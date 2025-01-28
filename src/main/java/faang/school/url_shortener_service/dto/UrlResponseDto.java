package faang.school.url_shortener_service.dto;

import faang.school.url_shortener_service.config.swagger.annotations.ApiUrlResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlResponseDto {
    @ApiUrlResponseDto
    private String shortUrl;
}