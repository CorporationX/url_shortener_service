package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.BaseEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final BaseEncoder baseEncoder;

    @Value("${batch.size}")
    private long batchSize;

    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<String> hashes = baseEncoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
    }
}
