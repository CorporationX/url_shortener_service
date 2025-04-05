package faang.school.urlshortenerservice.service;

import org.springframework.web.bind.MethodArgumentNotValidException;

public interface UrlService {
    String getUrlByHash(String hash) throws MethodArgumentNotValidException;

    String getHashByUrl(String url);

    void deleteOldHashes();
}
