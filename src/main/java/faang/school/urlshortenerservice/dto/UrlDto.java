package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @NotBlank(message = "URL can not be blank")
    @Pattern(regexp = "^(https?|http/ftp|mailto|file)://.*$",
            message = "URL must start with a valid protocol (http, https, ftp, mailto, file)")
    private String url;
}
