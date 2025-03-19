package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.number_hash_to_delete:1000}")
    @Setter
    private Integer numberHashToDelete;

    @Async("cachedThreadPool")
    @Transactional
    public void generateBatch() {
        List<Hash> hashes = new ArrayList<>();
        List<Long> numbers = hashRepository.getUniqueNumbers();
        base62Encoder.encode(numbers).forEach(str -> hashes.add(new Hash(str)));
        hashRepository.saveAll(hashes);
    }
}
