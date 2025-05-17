package faang.school.urlshortenerservice.andreev.service;

import faang.school.urlshortenerservice.andreev.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;

    @Value("${hash.batch.size}")
    private int batchSize;

    private List<String> getHashBatch() {
        return hashRepository.getHashBatch(batchSize);
    }
}
