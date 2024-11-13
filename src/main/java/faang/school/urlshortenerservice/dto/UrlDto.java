package faang.school.urlshortenerservice.dto;


import faang.school.urlshortenerservice.validation.annotation.Url;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlDto {

    @NotNull(message = "URL cannot be null")
    @Url(message = "URL is not valid")
    private String url;
}
