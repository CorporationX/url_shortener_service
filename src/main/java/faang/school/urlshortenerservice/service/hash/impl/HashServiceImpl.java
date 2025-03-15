package faang.school.urlshortenerservice.service.hash.impl;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.properties.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.hash.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final HashRepository hashRepository;
    private final UrlShortenerProperties properties;

    @Override
    public List<Long> getNewNumbers() {
        log.info("GetNewNumbers");
        return hashRepository.getUniqueNumbers(properties.getBatchSize());
    }

    @Override
    public void saveHashes(List<Hash> hashes) {
        log.info("SaveHashes, size: {}", hashes.size());
        hashRepository.saveAll(hashes);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public List<Hash> getBatchHashesAndDelete() {
        List<Hash> hashes = hashRepository.findAllLimit(0, properties.getBatchSize());
        hashRepository.deleteAll(hashes);

        log.info("GetBatchHashesAndDelete size: {}", hashes.size());
        return hashes;
    }
}
