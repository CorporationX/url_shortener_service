package faang.school.urlshortenerservice.cache.hash;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.concurrent.CompletableFuture;

public interface HashCache {
    CompletableFuture<String> getHashAsync();

}
