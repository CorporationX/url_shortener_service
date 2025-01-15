package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validator.UrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashCache hashCache;
    private final UrlValidator validator;

    @Transactional
    @Cacheable(value = "url", key = "#urlDto.url", unless = "#urlDto.url == null")
    public String getUrlHash(UrlDto urlDto) {
        String url = urlDto.getUrl();
        validator.validateUrl(urlDto.getUrl());
        String hash = hashCache.getHash();
        StringBuilder builder = new StringBuilder();
        builder.append(removePathAfterThirdSlash(url));
        builder.append(hash);
        Url smallUrl = new Url();
        smallUrl.setHash(hash);
        smallUrl.setUrl(url);
        urlRepository.save(smallUrl);
        return smallUrl.getUrl();
    }

    private String removePathAfterThirdSlash(String urlString) {
        int thirdSlashIndex = -1;
        int slashCount = 0;
        for (int i = 0; i < urlString.length(); i++) {
            if (urlString.charAt(i) == '/') {
                slashCount++;
                if (slashCount == 3) {
                    thirdSlashIndex = i;
                    break;
                }
            }
        }
        if (thirdSlashIndex != -1) {
            return urlString.substring(0, thirdSlashIndex + 1);
        }
        return urlString;
    }

}
