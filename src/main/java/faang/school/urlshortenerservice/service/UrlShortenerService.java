package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UrlShortenerService {
    private final UrlMapper urlMapper;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    public UrlDto convertToShortUrl(UrlDto originalUrl) {
        Url url = urlMapper.toEntity(originalUrl);
        url.setHash(hashCache.getHash().getHash());
        return urlMapper.toDto(url);
    }

    public UrlDto getOriginalUrl(String hash) {
        Url url = urlRepository.findById(hash).orElseThrow(
                () -> new EntityNotFoundException("There is no url with this hash " + hash));
        return urlMapper.toDto(url);
    }
}
