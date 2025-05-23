package faang.school.urlshortenerservice.service.hash.shedule;

import faang.school.urlshortenerservice.service.hash.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGenerationScheduler {
    private final HashGenerator hashGenerator;

    @Scheduled(fixedDelayString = "${hash.generation.interval}")
    public void scheduleGenerateHashesBatch() {
        hashGenerator.generateHashesBatch();
    }
}