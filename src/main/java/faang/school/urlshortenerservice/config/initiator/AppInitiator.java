package faang.school.urlshortenerservice.config.initiator;

import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppInitiator implements CommandLineRunner {

    private final HashGenerator hashGenerator;
    private final HashCache hashCache;
    @Override
    public void run(String... args) {
        hashGenerator.generateBatchIfNeeded();
        hashCache.refill();
    }
}
