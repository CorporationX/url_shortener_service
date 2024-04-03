package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.validator.url.UrlConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {
    @Size(max = 6)
    private String hash;
    @UrlConstraint
    private String url;
    private LocalDateTime createdAt;
}