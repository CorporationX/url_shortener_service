package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashGenerator {
    private final Base62Encoder base62Encoder;

    @Value("${hash.length:6}")
    private int hashLength;

    public List<String> generateBatch(List<Long> numbers) {
        log.debug("Generating batch of {} hashes", numbers.size());
        return base62Encoder.encode(numbers).stream()
                .map(hash -> padToLength(hash, hashLength))
                .collect(Collectors.toList());
    }

    private String padToLength(String hash, int length) {
        if (hash.length() >= length) {
            return hash;
        }
        return "0".repeat(length - hash.length()) + hash;
    }
}
