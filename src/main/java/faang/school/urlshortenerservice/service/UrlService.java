package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCache hashCache;
    private final UrlValidator urlValidator;
    private final UrlMapper urlMapper;
    private final HashMapper hashMapper;
    private final UrlRepository urlRepository;

    @Value("${url.host}")
    private String host;

    @Transactional
    public HashDto createShortLink(UrlDto urlDto) {
        if(urlValidator.validateUrlAlreadyExists(urlDto.getUrl())) {
            Url url = urlRepository.findByUrl(urlDto.getUrl());
            return convertToHashDto(url.getHash());
        }

        Url url = urlMapper.toEntity(urlDto);
        url.setHash(hashCache.getHash().getHash());
        urlRepository.save(url);
        return convertToHashDto(url.getHash());
    }

    private HashDto convertToHashDto(String hashValue) {
        Hash hash = new Hash(host + hashValue);
        return hashMapper.toDto(hash);
    }
}
