package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UrlDto {

    @NotBlank(message = "url cannot be blank")
    private String url;

}
