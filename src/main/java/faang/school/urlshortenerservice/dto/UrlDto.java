package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlDto {
    @Size(min = 1, max = 8)
    private String hash;

    @NotNull
    @Size(min = 1, max = 1024)
    private String url;

    private LocalDateTime createdAt;
}
