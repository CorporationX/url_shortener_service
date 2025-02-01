package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UrlCreateDto(

    @NotNull
    String url
) {

}
