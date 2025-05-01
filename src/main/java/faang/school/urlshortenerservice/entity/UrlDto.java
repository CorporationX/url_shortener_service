package faang.school.urlshortenerservice.entity;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UrlDto {
    // ^((http|https):\/\/)[-a-zA-Z0-9@:%._\\+~#?&\/=]*
    @Pattern(regexp= "^((http|https):/)[-a-zA-Z0-9@:%._+~#?&/=]*",
    message = "\"Некорректный URL\"")
    private String url;
}
