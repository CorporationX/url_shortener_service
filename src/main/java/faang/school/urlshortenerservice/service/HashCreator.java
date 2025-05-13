package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class HashCreator {

    @Async("hashesCreator")
    public CompletableFuture<List<Hash>> createHashes(List<String> uniqueStrings) {
        List<Hash> hashes = new ArrayList<>();
        for (String uniqueString : uniqueStrings) {
            hashes.add(new Hash(uniqueString));
        }
        return CompletableFuture.completedFuture(hashes);
    }
}
