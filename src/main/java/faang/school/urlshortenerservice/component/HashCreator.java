package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
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
