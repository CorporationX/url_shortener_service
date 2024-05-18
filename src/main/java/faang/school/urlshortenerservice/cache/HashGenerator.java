package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    @Value("${length.range:3}")
    private int length;

    @Value("${length.batchSize:100}")
    private int batchSize;

    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Transactional
    @Scheduled(cron = "${hash.cron:0 0 0 * * *}")
    public void generateUniqueHash() {
        for (int i = 0; i < 100; i += batchSize) {
            Set<Hash> hashes = IntStream.range(0, batchSize)
                    .parallel()
                    .mapToObj(j -> {
                        String base64Hash = generateRandomString();
                        return Hash.builder()
                                .base64Hash(Base64.getEncoder().encodeToString(base64Hash.getBytes()))
                                .build();
                    })
                    .collect(Collectors.toSet());
            hashRepository.saveAll(hashes);
        }
    }

    @Transactional
    public Set<String> getHashes(long amount) {
        Set<Hash> hashes = hashRepository.findAndDelete(amount); //PessimisticLock при запросе из базы
        if (hashes.size() < amount) {
            generateUniqueHash();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getBase64Hash).collect(Collectors.toSet());
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<Set<String>> getHashAsync(long amount) {
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String generateRandomString() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            sb.append(alphabet.charAt(index));
        }
        return sb.toString();
    }
}
