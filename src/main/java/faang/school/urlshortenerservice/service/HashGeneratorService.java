package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorService implements GeneratorService {

    private final HashJdbcRepository hashJdbcRepository;
    private final BaseEncoder baseEncoder;

    @Override
    public void generateHashBatch() {
        List<Long> uniqueNumbers = hashJdbcRepository.getUniqueNumbers();
        List<String> hashes = baseEncoder.encode(uniqueNumbers);
        hashJdbcRepository.save(hashes);
    }
}