package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final AsyncHashGenerator asyncHashGenerator;

    public void generateBatch() {
        asyncHashGenerator.generateBatchAsync();
    }
}
