package faang.school.urlshortenerservice.service.cache;

/**
 * Defines the contract for a local, in-memory cache responsible for storing and providing pre-generated hashes.
 * <p>
 * This cache is designed to provide fast, on-demand access to unique hashes, which can be used for URL shortening.
 * Implementations are expected to handle their own initialization and the logic for replenishing the cache when it
 * runs low on available hashes.
 */
public interface LocalCache {

    /**
     * Initializes the cache with a set of pre-generated hashes.
     */
    void initializeCache();

    /**
     * Retrieves and removes a single hash from the cache.
     * <p>
     * This method is expected to be thread-safe. If the cache is empty, this method will block for a
     * configurable amount of time.
     *
     * @return A unique hash string from the cache.
     * @throws faang.school.urlshortenerservice.exception.HashRetrievalTimeoutException if a hash cannot be obtained within the configured timeout.
     */
    String getHash();
}
