package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HashService {

    List<Hash> generateHashes(int size);

    CompletableFuture<List<Hash>> generateHashesAsync(int size);

    void saveHashes(List<Hash> hashes);

    List<Hash> readFreeHashes();

    CompletableFuture<List<Hash>> readFreeHashesAsync();

    void generateAndSaveHashes();


}
