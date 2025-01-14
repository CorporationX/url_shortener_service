package faang.school.urlshortenerservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupTask {

    private final HashGenerator hashGenerator;

    @Value("${hash.generator.range-size}")
    private int rangeSize;

    @PostConstruct
    public void init() {
        log.info("Checking if there are enough hashes");
        hashGenerator.generateAndSaveHashes(rangeSize);
    }
}
