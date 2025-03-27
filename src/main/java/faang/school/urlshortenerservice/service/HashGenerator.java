package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {

    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${batch.generation-size}")
    private int GENERATION_BATCH_SIZE;

    @Transactional
    public List<String> generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(GENERATION_BATCH_SIZE);
        List<String> hashes = baseEncoder.encodeBatch(numbers);
        List<Hash> toSave = hashes.stream().map(Hash::new).toList();
        hashRepository.saveAll(toSave);
        log.info("Generated and saved {} hashes.", hashes.size());
        return hashes;
    }

}
