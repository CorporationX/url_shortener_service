package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.range}")
    private int range;

    @Transactional
    @Async("hashGeneratorExecutorService")
    public void generateBatch(){
        List<Long> interval = hashRepository.getUniqueNumbers(range);
        List<String> hashes = base62Encoder.applyBase62Encoding(interval);
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount){
        List<String> hashes = hashRepository.getHashBatch(amount);
        if(hashes.size() < amount){
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes;
    }

    @Transactional
    @Async("hashGeneratorExecutorService")
    public CompletableFuture<List<String>> getHashesAsync(long amount){
        return CompletableFuture.completedFuture(getHashes(amount));
    }
}

