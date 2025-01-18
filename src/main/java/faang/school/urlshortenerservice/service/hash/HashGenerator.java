package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.Hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface HashGenerator {

    CompletableFuture<List<Hash>> generateBatch() throws ExecutionException, InterruptedException;
}
