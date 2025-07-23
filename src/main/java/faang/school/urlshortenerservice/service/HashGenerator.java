package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.generator.max_amount:10}")
    private Long maxAmount;
    @Value("${app.min-percent:3}")
    private int minPercent;
    @Value("${app.generator.coeff:10}")
    private int coeff;


    @Transactional
    public List<String> getHashes(Long range) {
        do {
            log.debug("Getting hashes for range {}", range);
            List<String> hashes = hashRepository.getPortionOfHashes(range);
            if (hashes.isEmpty()) {
                generateBatch(range * coeff);
            } else {
                return hashes;
            }
        } while (true);
    }

    @Transactional
    public void generateBatchBySchedule(Long range) {
        log.debug("Try generating batch of {} hashes", range);
        if (isNeedGenerate()) {
            generateBatch(range);
        } else {
            log.info("No batch of {} hashes", range);
        }
    }

    private void generateBatch(Long range) {
        List<Long> numbers = hashRepository.getUniqueNumbers(range);
        List<Hash> hashes = base62Encoder.encode(numbers).stream()
            .map(hash -> Hash.builder().hash(hash).build())
            .toList();
        hashRepository.saveAll(hashes);
    }

    private boolean isNeedGenerate() {
        Long size = hashRepository.countAll();
        log.info("count hash in DB: {}", size);
        return size < (maxAmount * minPercent / 100);
    }
}
