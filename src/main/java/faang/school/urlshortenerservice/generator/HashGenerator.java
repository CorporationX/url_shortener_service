package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final HashRepository hashRepository;

    @Value("${hash.max-range}")
    private int maxRange;

    @Transactional
    public List<String> getHashBatch(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    private void generateHash() {
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = numbers.stream()
                .map(this::applyBase62Encoding)
                .toList();

        hashRepository.saveAll(hashes);
    }

    private Hash applyBase62Encoding(long number) {
        StringBuilder result = new StringBuilder();
        long num = number;
        while (num > 0) {
            result.append(BASE_62_CHARACTERS.charAt((int) (num % BASE_62_CHARACTERS.length())));
            num /= BASE_62_CHARACTERS.length();
        }

        return new Hash(result.toString());
    }
}
