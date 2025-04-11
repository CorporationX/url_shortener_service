package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Transactional
    public void generateHashes(int batchSize) {
        List<Hash> hashList = hashRepository.getUniqueNumbers(batchSize).stream()
                .map(n -> new Hash(Base62Encoder.encode(n)))
                .toList();

        hashRepository.saveAll(hashList);
    }

    @Transactional
    public Mono<List<String>> getHashes(int size) {
        if (size > getHashCount()) {
            generateHashes(size);
        }

        return Mono.fromCallable(() -> hashRepository.findAndDeleteBySize(size))
                .map(hashes -> hashes.stream()
                        .map(Hash::getHash)
                        .toList()
                );
    }

    @Transactional(readOnly = true)
    public Long getHashCount() {
        return hashRepository.count();
    }
}
