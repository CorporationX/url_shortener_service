package faang.school.urlshortenerservice.dto;


import faang.school.urlshortenerservice.validation.annotation.Url;
import lombok.Data;

@Data
public class UrlDto {

    @Url(message = "URL is not valid")
    private String url;
}
