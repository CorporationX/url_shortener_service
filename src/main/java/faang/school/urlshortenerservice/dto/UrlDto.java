package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @NotEmpty(message = "URL не должен быть пустым")
    private String url;
}
