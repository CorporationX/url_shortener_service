package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.repository.UniqueldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final UniqueldRepository uniqueldRepository;

    @Scheduled(cron = "* * *")
    public void generate(URL){
        //base62 algo
        uniqueldRepository.setUrl;
    }
}
