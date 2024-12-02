package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.validation.ValidUrl;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @NotEmpty(message = "URL must not be blank")
    @ValidUrl
    private String url;
}
