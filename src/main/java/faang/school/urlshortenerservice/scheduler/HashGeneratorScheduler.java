package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {
    private final HashGenerator hashGenerator;
    @Value("${hashes.amount}")
    private int hashesAmount;

    @Scheduled(cron = "${schedulers.hash_generator.cron}")
    public void generateHashes(){
        hashGenerator.generateAndSaveHashes();
    }

}
