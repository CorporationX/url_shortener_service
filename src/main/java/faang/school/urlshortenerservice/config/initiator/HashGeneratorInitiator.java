package faang.school.urlshortenerservice.config.initiator;

import faang.school.urlshortenerservice.service.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGeneratorInitiator implements CommandLineRunner {

    private final HashGenerator hashGenerator;
    @Override
    public void run(String... args) {
        hashGenerator.generateBatchIfNeeded();
    }
}
