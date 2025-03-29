package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public List<Hash> getHashes(long count) {
        List<Hash> hashes = hashRepository.getHashBatch(count);

        if (hashes.size() < count) {
            hashGenerator.generateBatch();
            hashes = hashRepository.getHashBatch(count);
        }

        return hashes;
    }
}
