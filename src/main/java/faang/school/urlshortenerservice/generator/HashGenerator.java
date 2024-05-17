package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseConversion;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class HashGenerator {

    @Value("${app.hash-batch-size}}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final BaseConversion baseConversion;

    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getFollowingRangeUniqueNumbers(batchSize);
        List<Hash> hashes = baseConversion.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();
    }
}
