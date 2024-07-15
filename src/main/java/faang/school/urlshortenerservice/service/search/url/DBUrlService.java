package faang.school.urlshortenerservice.service.search.url;

import faang.school.urlshortenerservice.repository.url.UrlRepository;
import faang.school.urlshortenerservice.service.search.SearchesService;
import faang.school.urlshortenerservice.entity.url.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Order(2)
@RequiredArgsConstructor
public class DBUrlService implements SearchesService {
    private final UrlRepository urlRepository;

    @Override
    public Optional<String> findUrl(String hash) {
        return urlRepository.findByHash(hash).map(Url::getUrl);
    }
}
