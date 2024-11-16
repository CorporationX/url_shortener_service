package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Transactional
    public void saveAllBatch(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> findAllByPackSize(int packSize) {
        return hashRepository.findAllAndDeletePack(packSize);
    }

    @Transactional
    public List<Long> getUniqueNumbers(int size) {
        return hashRepository.getUniqueNumbers(size);
    }

    @Transactional(readOnly = true)
    public Long getHashesSize() {
        return hashRepository.getHashesSize();
    }
}
