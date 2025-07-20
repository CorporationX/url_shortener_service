package faang.school.urlshortenerservice.cache;

import jakarta.validation.constraints.NotBlank;

/**
 * An interface for caching URLs by their hashes.
 *
 * <p>Allows storing a URL associated with a unique string hash.
 * Implementations may use in-memory storage, files, databases, etc.</p>
 */
public interface UrlCache {
    /**
     * Saves the specified URL under the given hash.
     *
     * @param hash the unique hash key under which the URL will be stored
     * @param url  the URL string to save
     * @throws IllegalArgumentException if {@code hash} or {@code url} is {@code null} or blank
     */
    void addToCache(@NotBlank String hash, @NotBlank String url);
}
