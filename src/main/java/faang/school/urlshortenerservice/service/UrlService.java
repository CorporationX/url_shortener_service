package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlCasheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCash hashCash;
    private final UrlRepository repository;
    private final UrlCasheRepository casheRepository;

    public String shortUrl(String url){
        String shortUrl = hashCash.getHash();
        repository.save(new Url(shortUrl, url));
        casheRepository.saveUrlHash(shortUrl, url);
        return shortUrl;
    }
}
