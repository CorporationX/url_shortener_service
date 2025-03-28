package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.service.UrlCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Log4j2
@Component
@RequiredArgsConstructor
public class CleanerScheduler {

  private final UrlCleaner urlCleaner;

  @Scheduled(cron = "${url-shortener-service.cleaner-cron}")
  public void scheduleCleanExpiredUrls() {
    urlCleaner.cleanExpiredUrls();
  }
}