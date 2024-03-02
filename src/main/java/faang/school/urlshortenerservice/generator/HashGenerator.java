package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.base62encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range:10000}")
    private int range;

    @Async("taskExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(range);
        List<String> encoded = base62Encoder.encodeList(uniqueNumbers);
        List<Hash> hashes = encoded.stream().map(Hash::new).toList();
        hashRepository.saveAll(hashes);
    }

}
