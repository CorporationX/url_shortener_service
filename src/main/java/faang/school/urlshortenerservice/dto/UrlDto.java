package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UrlDto (

   @Pattern(regexp = "^(https?://)?(localhost|\\d{1,3}(\\.\\d{1,3}){3}|" +
           "([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+[a-zA-Z]{2,})(:\\d+)?(/\\S*)?$", message = "Invalid URL format")
    String url
){
}
