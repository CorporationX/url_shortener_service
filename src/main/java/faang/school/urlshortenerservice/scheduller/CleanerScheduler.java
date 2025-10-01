package faang.school.urlshortenerservice.scheduller;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.CleanHashException;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Scheduled(cron = "${remove-old-hashes-crone}")
    public void removeOldHashes() {
        List<Url> oldUrls = urlRepository.findOldUrls();
        try {
            List<Hash> returnedHashes = oldUrls.stream()
                    .map(u -> new Hash(u.getHash()))
                    .toList();
            hashRepository.saveAll(returnedHashes);
            urlRepository.deleteAll(oldUrls);
        } catch(Exception e) {
            throw new CleanHashException("Во время очистки хешей произошла ошибка");
        }
    }
}
