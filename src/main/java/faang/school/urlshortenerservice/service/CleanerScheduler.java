package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${application.schedule.period:1:0:0:0:0}")
    private String period; //Значение должно быть вида "y:m:w:d:h"

    @Scheduled(cron = "${application.schedule.cleaner:0 0 0 * * *}")
    @Transactional
    public void clean() {
        log.info("Staring cleaning old urls");
        LocalDateTime targetDate = getTargetDate();
        List<Url> oldUrls = urlRepository.getAndDeleteAllByCreatedAtBefore(targetDate);
        List<Hash> hashes = oldUrls.stream()
                .map((url) -> new Hash(url.getHash()))
                .toList();
        hashRepository.saveAll(hashes);
    }

    private LocalDateTime getTargetDate() {
        int[] values = Arrays.stream(period.split(":"))
                .mapToInt(Integer::parseInt)
                .toArray();
        return LocalDateTime.now()
                .minusYears(values[0])
                .minusMonths(values[1])
                .minusWeeks(values[2])
                .minusDays(values[3])
                .minusHours(values[4]);
    }
}
