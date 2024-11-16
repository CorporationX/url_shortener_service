package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.generate.HashGenerator;
import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public void putNewHash(int maxRange) {
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = hashGenerator.generateBatch(numbers);
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<Hash> getHashes(int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }
}
