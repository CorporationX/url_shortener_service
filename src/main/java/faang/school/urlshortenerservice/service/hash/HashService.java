package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.hash.Hash;
import faang.school.urlshortenerservice.repository.postgres.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Transactional
    public void saveAll(List<String> hashes) {
        List<Hash> entities = hashes.stream()
                .map(this::build)
                .toList();
        hashRepository.saveBatch(entities);
    }

    private Hash build(String hash) {
        return Hash.builder()
                .hash(hash)
                .build();
    }
}
