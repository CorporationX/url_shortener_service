package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.HashFree;
import faang.school.urlshortenerservice.repository.hash.HashFreeRepository;
import faang.school.urlshortenerservice.service.batchsaving.BatchSaveService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashFreeService {
    private final HashFreeRepository hashFreeRepository;
    private final BatchSaveService batchSaveService;

    @Setter
    @Value("${value.getBatchSize}")
    private int getBatchSize;

    @Transactional
    public List<String> getHashBatch() {
        return hashFreeRepository.getRandomHashFree(getBatchSize).stream().map(HashFree::getHash).toList();
    }

    @Transactional
    public void moderateHash() {
        List<String> hashes = hashFreeRepository.deleteAndGetOldHash();
        log.info("Old hashes {}", hashes);
        batchSaveService.saveEntities(hashes, HashFree.class);
        log.info("hashes saved");
    }
}
