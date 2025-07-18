package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;

public interface UrlService {

    /**
     * Creates a short URL for the given long URL. This process involves three main steps:
     * <ol>
     *     <li>Retrieve a pre-generated, unique hash from the local in-memory cache.</li>
     *     <li>Atomically save the mapping between the hash and the long URL to the primary database.</li>
     *     <li>Cache the newly created mapping in a distributed cache (Redis) with a configured Time-To-Live (TTL) to accelerate future lookups.</li>
     * </ol>
     * This method is transactional. If any step fails, the entire operation is rolled back to ensure data consistency.
     *
     * @param longUrl The original, long URL to be shortened.
     * @return The unique hash representing the short URL.
     * @throws RuntimeException if the process fails at any stage, wrapping the original exception.
     */
    String getShortUrl(String longUrl);

    /**
     * Retrieves the original (long) URL associated with the given hash.
     * <p>
     * This method delegates the retrieval to the {@code urlRetrieverService}. If the service
     * cannot find a URL for the given hash, it throws a {@link UrlNotFoundException}.
     *
     * @param hash The unique hash identifying the shortened URL.
     * @return The original, long URL as a {@code String}.
     * @throws UrlNotFoundException if no URL is found for the provided hash.
     */
    String getLongUrl(String hash);
}
