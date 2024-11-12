package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashGeneratorService {

    CompletableFuture<List<Hash>> generateBatch();
}
