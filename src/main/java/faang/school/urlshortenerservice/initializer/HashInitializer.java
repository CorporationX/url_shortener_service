package faang.school.urlshortenerservice.initializer;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashInitializer {
    private final HashGenerator hashGenerator;

    @PostConstruct
    public void initializeCache() {
        hashGenerator.generateBatch();
        log.info("Cache generation completed using @PostConstruct.");
    }
}
