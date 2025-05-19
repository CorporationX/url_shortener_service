package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.properties.HashGenerationProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {


    private final HashGenerationProperties properties;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    @Scheduled(cron = "#{@hashGenerationProperties.cron}")
    public void generateHashes() {
        List<Long> uniqueNumber = hashRepository.getUniqueHashNumbers(properties.getGenerationRange());
        hashRepository.saveAll(convertNumberToHash(uniqueNumber));
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<String>> getHashesAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> freeHashes = hashRepository.findAndDelete(amount);
        if (freeHashes.size() < amount){
            generateHashes();
            freeHashes.addAll(hashRepository.findAndDelete(amount - freeHashes.size()));
        }
        return  convertHashesToString(freeHashes);
    }

    private List<Hash> convertNumberToHash(List<Long> uniqueNumber) {
        return uniqueNumber.stream()
                .map(base62Encoder::applyBase62Encoding)
                .map(Hash::new)
                .toList();
    }

    private List<String> convertHashesToString(List<Hash> hashes){
        return hashes.stream().map(Hash::getHash).toList();
    }

}
