package faang.school.urlshortenerservice.schedul;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final UrlService urlService;
    private final HashGenerator generator;

    @Scheduled(cron = "${remove_old_url_cron}")
    public void removeOldUrl(){
        urlService.removeOldUrl();
    }

    @Scheduled(cron = "${hash.cache.generate_hash_time}")
    public void generateHash(){
        generator.generateHash();
    }
}
