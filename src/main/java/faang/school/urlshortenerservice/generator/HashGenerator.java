package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE64_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final HashRepository hashRepository;

    @Value("${hash.range:10000}")
    private int maxGeneratedHash;

    @Value("${hash.get:1000}")
    private int maxGetHashes;

    @Value("${hash.min:500}")
    private int minGetHashes;

    @Transactional
    public void generateHash() {
        hashRepository.saveAll(hashRepository.getSequences(maxGeneratedHash).stream()
                .map(this::applyBase64Encoding)
                .map(FreeHash::new)
                .toList());
    }

    @Transactional
    public List<String> getListFreeHash() {
        List<String> hashes = hashRepository.findAndDelete(maxGetHashes);

        if (hashes.size() < minGetHashes) {
            generateHash();
            hashes.addAll(hashRepository.findAndDelete(maxGetHashes - hashes.size()));
        }

        return hashes;
    }

    private String applyBase64Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE64_CHARACTERS.charAt((int) (number % BASE64_CHARACTERS.length())));
            number /= BASE64_CHARACTERS.length();
        }
       return builder.toString();
    }
}
