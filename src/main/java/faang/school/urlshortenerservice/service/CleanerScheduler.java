package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {
    private final HashRepository hashRepository;
    @Value("${hash.clean.days}")
    Integer days;

    @Scheduled(cron = "${hash.clean.cron}")
    public void clearHashes() {
        LocalDateTime time = LocalDateTime.now().minusDays(days);
        var freeHashes = hashRepository.findAndDeleteOldHashes(time);
        hashRepository.saveHashes(freeHashes);
    }
}
