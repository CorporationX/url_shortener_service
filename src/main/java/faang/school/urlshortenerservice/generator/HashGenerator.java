package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${uniqueNumbers.amount}")
    private int amount;

    private final HashRepository hashRepository;

    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(amount);

    }
}
