package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache cache;
    private final UrlRepository urlRepository;

    @Override
    public void saveAssociation(UrlDto dto) {

    }
}
