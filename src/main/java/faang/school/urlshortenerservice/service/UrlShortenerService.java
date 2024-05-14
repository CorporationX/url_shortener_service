package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlDto shortenUrl(UrlDto originUrl){

        //should I validate here if this url already exists in Repo??

        Url url = urlMapper.toEntity(originUrl);
        url.setHash(hashCache.getHash().getHash());

        return urlMapper.toDto(urlRepository.save(url));
    }

    public UrlDto getOriginalLink(String hash) {
        Url url = urlRepository.findById(hash).orElseThrow(
                () -> new EntityNotFoundException("Url with hash: " + hash + "not found!"));
        return urlMapper.toDto(url);
    }
}