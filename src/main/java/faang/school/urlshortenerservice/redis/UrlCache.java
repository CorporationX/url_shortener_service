package faang.school.urlshortenerservice.redis;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UrlCache {

    public void saveInCache(Url shortUrl) {
        //TODO: реализовать в задаче с кешем url'ов
    }

    public Optional<Url> findByHash(String hash) {
        return Optional.empty();
    }
}