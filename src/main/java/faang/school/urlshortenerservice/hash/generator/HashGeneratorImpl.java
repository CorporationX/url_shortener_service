package faang.school.urlshortenerservice.hash.generator;

import faang.school.urlshortenerservice.hash.encoder.HashEncoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {

    private final HashRepository hashRepository;
    private final HashEncoder hashEncoder;
    @Value("${generator.uniqueNumbersAmount}")
    private int uniqueNumbersAmount;

    @Override
    public void generateHash() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(uniqueNumbersAmount);
        Set<String> hashes = hashEncoder.encode(uniqueNumbers);
        hashRepository.save(hashes);
    }
}
