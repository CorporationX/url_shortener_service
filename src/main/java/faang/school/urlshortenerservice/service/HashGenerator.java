package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Async("taskExecutor")
    @Transactional
    public void generateBatch(@Value("${spring.application.sequence.batch-size}") int batchSize) {
        List<Long> emptyIds = hashRepository.findByValueIsNull(PageRequest.of(0, batchSize))
                .stream()
                .map(Hash::getId)
                .toList();

        List<Hash> hashes = encoder.encodeSequence(emptyIds);
        hashRepository.saveAll(hashes);

        log.info("Generated new hash sequence from id: {} to id: {}",
                emptyIds.get(0), emptyIds.get(emptyIds.size() - 1));
    }
}
