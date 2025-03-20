package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;

    public void saveHashes(List<String> hashes) {
        hashRepository.batchInsert(
                hashes.stream().map(Hash::new).toList()
        );
    }
}
