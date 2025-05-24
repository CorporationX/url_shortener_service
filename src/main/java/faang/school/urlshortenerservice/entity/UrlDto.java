package faang.school.urlshortenerservice.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UrlDto {
    @Pattern(regexp= "^((http|https):/)[-a-zA-Z0-9@:%._+~#?&/=]*",
    message = "Wrong URL format")
    @NotNull(message = "URL should not be empty")
    private String url;
}
