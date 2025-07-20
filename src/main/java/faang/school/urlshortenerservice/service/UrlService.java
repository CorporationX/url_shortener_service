package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for processing, retrieving URLs.
 *
 * <p>Provides methods to get a short hash for long URL, fetch the original
 * URL by its hash, and delete old hash entries before a specified cutoff date,
 * returning the list of deleted hashes.</p>
 */
public interface UrlService {
    /**
     * Returns a unique hash for the given long URL DTO.
     *
     * @param urlDto the DTO containing the long URL; must not be {@code null}
     * @return the generated hash string representing the shortened URL
     * @throws IllegalArgumentException if {@code urlDto} is {@code null}
     */
    String processLongUrl(@NotNull(message = "UrlDto cannot be NULL") UrlDto urlDto);

    /**
     * Retrieves the original long URL associated with the specified hash.
     *
     * @param hash the unique hash key; must not be {@code null} or blank
     * @return the original long URL corresponding to the given hash
     * @throws IllegalArgumentException if {@code hash} is {@code null} or blank
     */
    String getOriginalUrl(@NotBlank String hash);

    /**
     * Deletes up to {@code batchLimit} hash entries and associated urls that are older than the given cutoff
     * and returns their hash values.
     *
     * @param cutoff     the cutoff {@link LocalDateTime}; entries before this date will be deleted; must not be {@code null}
     * @param batchLimit the maximum number of entries to delete; must be at least 1
     * @return a list of hash strings that were deleted, up to the specified batch limit
     * @throws IllegalArgumentException if {@code cutoff} is {@code null} or {@code batchLimit} is less than 1
     */
    List<String> deleteOldReturningHashes(@NotNull LocalDateTime cutoff, @Min(1) int batchLimit);
}