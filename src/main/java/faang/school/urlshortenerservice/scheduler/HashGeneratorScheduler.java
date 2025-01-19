package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGeneratorScheduler {

  private final HashGenerator hashGenerator;

  @PostConstruct
  public void init() {
    hashGenerator.generateBatch();
  }

}
