package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.hash.HashCache;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;
    private final UrlCacheRepository urlCacheRepository;

    @Transactional
    public UrlResponseDto createShortUrl(UrlRequestDto url) {
        Url entity = urlMapper.toEntity(url);
        entity.setHash(hashCache.getHash());
        Url savedUrl = urlRepository.save(entity);
        UrlResponseDto savedUrlDto = urlMapper.toResponseDto(savedUrl);
        urlCacheRepository.set(savedUrl.getHash(), savedUrlDto);

        return savedUrlDto;
    }

    @Override
    public UrlResponseDto getUrl(String hash) {
        UrlResponseDto urlDto = urlCacheRepository.get(hash);
        if (urlDto != null) {
            return urlDto;
        }

        return urlRepository.findById(hash)
                .map(urlMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Url with hash [%s] not found", hash)));
    }

    @Override
    public List<String> retrieveOldUrls(int daysCount) {
        return urlRepository.retrieveOldUrls(daysCount);
    }
}
