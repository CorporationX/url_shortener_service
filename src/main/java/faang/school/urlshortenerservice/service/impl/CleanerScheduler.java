package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.repository.impl.HashRepositoryImpl;
import faang.school.urlshortenerservice.repository.impl.UrlRepositoryImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Log4j2
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

  private final UrlRepositoryImpl urlRepository;
  private final HashRepositoryImpl hashRepository;

  @Scheduled(cron = "${url-shortener-service.cleaner-cron}")
  @Transactional // не будет работать, вынести в отдельный компонент
  public void cleanExpiredUrls() {
    log.info("Clearing expired URLs...");
    List<String> expiredHashes = urlRepository.deleteExpiredUrls();

    if (expiredHashes.isEmpty()) {
      log.info("Nothing to clear");
      return;
    }
    hashRepository.save(expiredHashes);
    log.info("Clearing completed. Deleted: {} rows, hashes saved",
        expiredHashes.size());
  }
}
