package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("taskExecutor")
    public void generateBatch(int n) {
        List<Long> numbers = hashRepository.getUniqueNumbers(n);
        List<Hash> hashes = base62Encoder.encode(numbers).stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }
}
