package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlDto {

    @NotNull
    public String hash;

    @NotBlank
    public String url;

    private LocalDateTime created;
}
