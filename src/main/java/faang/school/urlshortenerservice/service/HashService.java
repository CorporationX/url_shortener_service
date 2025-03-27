package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.ChangeHashSequenceRepository;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;
    private final ChangeHashSequenceRepository changeHashSequenceRepository;
    private final HashGenerator hashGenerator;
    @Value("${hash-generator.length:5}")
    private Integer hashLength;
    @Value("${hash-generator.alphabet}")
    private String hashAlphabet;

    @PostConstruct
    public void checkHashLength() {
        long currentMaxSequenceNumber = hashRepository.getSequenceMax();
        long currentMinSequenceNumber = hashRepository.getSequenceMin();
        long newMaxSequenceNumber = getMaxSequenceNumber();
        long newMinSequenceNumber = getMinSequenceNumber();

        if (newMinSequenceNumber != currentMinSequenceNumber) {
            log.warn("Min sequenceNumber {} will changed", currentMinSequenceNumber);
            changeHashSequenceRepository.setSequenceMinValue(newMinSequenceNumber);
        }

        if (newMaxSequenceNumber != currentMaxSequenceNumber) {
            log.warn("Max sequenceNumber {} will changed", currentMaxSequenceNumber);
            changeHashSequenceRepository.setSequenceMaxValue(newMaxSequenceNumber);
        }
    }

    @Transactional
    public List<String> getHashes(int count) {
        log.info("Getting {} hashes from DB", count);
        if (hashRepository.count() == 0) {
            log.warn("Hash repository is empty");
            hashGenerator.generateBatch();
        }

        List<String> hashes = hashRepository.getHashBatch(count);
        if (hashes.size() < count) {
            hashGenerator.generateBatch();
        }
        log.info("Get {} hashes from DB", count);
        return hashes;
    }

    @Transactional
    public long addHashList(List<String> hashList) {
        log.info("Adding hashes count: {}", hashList.size());

        List<Hash> hashes = hashList.stream()
                .map(hashString -> Hash.builder()
                        .hash(hashString)
                        .build())
                .toList();
        return hashRepository.saveAll(hashes).size();
    }

    private Long getMaxSequenceNumber() {
        return (long) (Math.pow(hashAlphabet.length(), hashLength) - 1);
    }

    private Long getMinSequenceNumber() {
        return (long) Math.pow(hashAlphabet.length(), hashLength - 1);
    }
}
