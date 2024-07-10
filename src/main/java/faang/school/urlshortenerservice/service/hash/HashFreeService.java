package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.hash.HashFree;
import faang.school.urlshortenerservice.repository.hash.HashFreeRepository;
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

    @Setter
    @Value("${value.getBatchSize}")
    private int bathSize;

    @Transactional
    public List<String> getHashBatch() {
        return hashFreeRepository.getRandomHashFree(bathSize).stream().map(HashFree::getHash).toList();
    }
}
