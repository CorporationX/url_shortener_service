package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanScheduler {

  private final UrlRepository urlRepository;
  private final HashRepository hashRepository;

  @PostConstruct
  @Scheduled(cron = "${cron.clean-db}")
  @Transactional
  public void clean() {
    List<String> oldHashes = urlRepository.delete();
    hashRepository.save(oldHashes);
  }

}
