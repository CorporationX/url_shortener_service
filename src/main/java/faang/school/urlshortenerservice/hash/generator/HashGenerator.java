package faang.school.urlshortenerservice.hash.generator;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashGenerator {
    CompletableFuture<List<Hash>> generateBatch();
}
