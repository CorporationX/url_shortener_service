package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashProperties hashProperties;

    public void generate() {
        List<Long> numbers = hashRepository.getUniqueNumbers(hashProperties.getGenerateSize());
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);
    }
}
