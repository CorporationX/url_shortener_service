package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.HashSaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private static final char[] BASE62 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    @Value("${uniqueNumbers}")
    private int uniqueNumbers;
    private final HashRepository repository;
    private final HashSaveRepository saveRepository;

    @Transactional
    public void generateHash() {
        List<Long> unique = repository.getUniqueNumbers(uniqueNumbers);
        List<Hash> hashes = unique
                .stream()
                .map(uniq -> encode(uniq))
                .map(uniq -> new Hash(uniq))
                .toList();
        saveRepository.save(hashes);
        log.info("Hashes generated successfully {}", hashes);
    }

    @Transactional
    @Async("batchExecutor")
    public CompletableFuture<List<Hash>> getHashes(long amount) {
        List<Hash> hashes = repository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(repository.findAndDelete(amount - hashes.size()));
        }
        saveRepository.save(hashes);
        return CompletableFuture.completedFuture(hashes);
    }

    private String encode(long number) {
        StringBuilder string = new StringBuilder();
        do {
            int remainder = Math.round(number % 62);
            string.insert(0, BASE62[remainder]);
            number /= 62;
        } while (number > 0);
        return string.toString();
    }
}
