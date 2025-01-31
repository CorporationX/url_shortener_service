package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.base62.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    @Value("${unique-number}")
    private long uniqueNumber;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("async")
    public void generateBatch() {
            List<Long> nums = hashRepository.getUniqueNumbers(uniqueNumber);
            List<Hash> hashes = base62Encoder.encode(nums);
            hashRepository.saveAll(hashes);
    }
}
