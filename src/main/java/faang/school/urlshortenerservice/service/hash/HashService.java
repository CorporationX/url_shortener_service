package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.jpa.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashRepository hashRepository;

    @Transactional
    public List<Hash> getHashesBatch(int batchSize) {
        return hashRepository.getHashesBatch(batchSize);
    }

    public void saveAll(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }
}
