package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.UrlAssociation;
import faang.school.urlshortenerservice.generator.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.validate.UrlValidate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final UrlValidate validator;
    private final HashCache hashCache;
    private final UrlRepository urlRepository;

    public String generateShortUrl(String originalUrl) {
        URL url = validator.getValidUrl(originalUrl);

        Hash hash = hashCache.getHash();

        UrlAssociation urlAssociation = new UrlAssociation();
        urlAssociation.setUrl(originalUrl);
        urlAssociation.setHash(hash.getHash());

        urlRepository.save(urlAssociation);

       return url.getProtocol() + "://" + url.getHost() + "/" + hash;
    }

    public String returnFullUrl(String shortUrl) {
        URL url = validator.getValidUrl(shortUrl);

        UrlAssociation urlAssociation = urlRepository.findById(url.getPath()).orElseThrow(
                () -> new IllegalStateException("For the specified hash the full URL is not in the database"));

        return urlAssociation.getUrl();
    }
}
