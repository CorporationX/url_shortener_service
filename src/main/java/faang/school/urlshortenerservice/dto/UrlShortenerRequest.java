package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UrlShortenerRequest(@NotBlank
                                  @Size(min = 9)
                                  @Pattern(regexp = "^https?:\\/\\/.+\\..+$", message = "Url invalid")
                                  String endPoint) {
}
