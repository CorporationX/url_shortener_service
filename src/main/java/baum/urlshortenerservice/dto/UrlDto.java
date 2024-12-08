package baum.urlshortenerservice.dto;

import baum.urlshortenerservice.validator.annotaiton.Url;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @NotNull
    @Url
    private String url;
}
