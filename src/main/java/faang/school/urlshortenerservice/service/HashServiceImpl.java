package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Transactional
    @Override
    public void performCronTaskTransactional(int createdBeforeMonths) {
        LocalDate now = LocalDate.now();
        log.info("Cron task executed at: " + now);
        List<Long> shouldBeDeleted = hashRepository.insertToHashDeletedUrls(
                now.minus(createdBeforeMonths, ChronoUnit.MONTHS), now);
        if (shouldBeDeleted == null || shouldBeDeleted.isEmpty()) {
            log.info("No URLs found for deletion.");
            return;
        }

        urlRepository.deleteAllByIdInBatch(shouldBeDeleted);
        log.info("Successfully deleted {} URLs.", shouldBeDeleted.size());
    }
}
