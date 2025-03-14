package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.repository.HashJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorService implements GeneratorService {

    private final HashJdbcRepository hashJdbcRepository;
    private final BaseEncoderService baseEncoderService;

    @Override
    public void generateHashBatch() {
        List<Long> uniqueNumbers = hashJdbcRepository.getUniqueNumbers();
        List<String> hashes = baseEncoderService.encode(uniqueNumbers);
        hashJdbcRepository.save(hashes);
    }
}