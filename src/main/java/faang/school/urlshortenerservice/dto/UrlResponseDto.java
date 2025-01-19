package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UrlResponseDto(

    @NotNull
    String url,

    @NotNull
    String hash,

    LocalDateTime createdAt

) {

}
