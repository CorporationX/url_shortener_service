package faang.school.urlshortenerservice.andreev.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    @NotNull(message = "Url can't not be null")
    private String url;
}
