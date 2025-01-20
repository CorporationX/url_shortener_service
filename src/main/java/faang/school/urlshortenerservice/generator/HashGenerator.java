package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base64Encoder;

    @Value("${hash.range:1000}")
    private long maxRange;

    @Transactional
    public void generateBatch(){
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = base64Encoder.encode(numbers).stream()
                .map(hash -> Hash.builder().hash(hash).build()).toList();
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount){
        List<String> hashes = hashRepository.getHashBatch(amount);
        if (hashes.size() < amount){
            generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount-hashes.size()));
        }
        return hashes;
    }

    @Transactional
    @Async("hashGeneratorExecutor") //TODO
    public CompletableFuture<List<String>> getHashesAsync(long amount){
        return CompletableFuture.completedFuture(getHashes(amount));
    }

}
