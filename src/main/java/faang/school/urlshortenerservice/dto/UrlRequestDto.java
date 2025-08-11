package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import faang.school.urlshortenerservice.validation.ValidUrl;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {

    @NotBlank(message = "URL cannot be empty")
    @ValidUrl
    private String url;
}