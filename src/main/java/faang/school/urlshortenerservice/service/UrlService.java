package faang.school.urlshortenerservice.service;

import java.net.URI;

import org.springframework.stereotype.Service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlDto generateUrl(URI uri) {
        String hash = hashCache.getHash();
        Url url = new Url();
        url.setHash(hash);
        url.setUrl(uri.toString());

        urlRepository.save(url);

        return urlMapper.toDto(url);
    }
}
