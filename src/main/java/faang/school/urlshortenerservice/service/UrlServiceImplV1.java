package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.enity.Url;
import faang.school.urlshortenerservice.hash.LocalHash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlServiceImplV1 implements UrlService{
    private final LocalHash localHash;
    private final UrlRepository urlRepository;

    @Value(value = "${hash.time.saving.day:100}")
    private int savingDays;

    @Override
    public String saveUrl(String url) {
        // check in redis
        return urlRepository.findByUrl(url)
                .orElseGet(() -> urlRepository.save(Url.builder()
                        .url(url)
                        .hash(localHash.getHash())
                        .last_get_at(LocalDateTime.now())
                        .build()))
                .getHash();
    }

    @Override
    public String getUrl(String hash) {
        // check in redis
        return urlRepository.findByHash(hash)
                .orElseThrow(() -> new EntityNotFoundException("url by hash " + hash + " does not exists"))
                .getUrl();

    }

    @Override
    public String getHash(String url) {
        // check in redis
        return urlRepository.findByUrl(url)
                .orElseThrow(() -> new EntityNotFoundException("url " + url + " does not exists"))
                .getHash();
    }

    @Override
    public void deleteUnusedUrl() {
        // delete in redis
        urlRepository.deleteUnusedUrl(LocalDateTime.now().minusDays(savingDays));
    }
}
