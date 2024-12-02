package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.service.hash.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public void saveAllBatch(List<Hash> hashes) {
        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> findAllByPackSize(int packSize) {
        List<String> hashes = hashRepository.findAllAndDeleteByPackSize(packSize);

        if (hashes.size() < packSize) {
            int size = packSize - hashes.size();
            hashes.addAll(hashGenerator.generateAndGet(size));
        }
        return hashes;
    }

    @Transactional
    public List<Long> getUniqueNumbers(long size) {
        return hashRepository.getUniqueNumbers(size);
    }

    @Transactional(readOnly = true)
    public Long getHashesSize() {
        return hashRepository.getHashesSize();
    }
}
