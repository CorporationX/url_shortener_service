package faang.school.urlshortenerservice.generator;

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

    @Value("${hash.range}")
    private int range;

    @Async("asyncGenerator")
    @Transactional
    public void generateHash() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(range);
        List<Hash> hashes = base62Encoder.encodeList(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
