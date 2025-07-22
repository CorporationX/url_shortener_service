package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public interface UrlService {
    String processLongUrl(@NotNull(message = "UrlDto cannot be NULL") UrlDto urlDto);

    String getOriginalUrl(@NotNull String hash);

    List<String> deleteOldReturningHashes(LocalDateTime cutoff, @Min(1) int batchLimit);
}
