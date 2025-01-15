package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.UrlAssociation;
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
    private final HashGenerator hashGenerator;
    private final UrlRepository urlRepository;

    public String generateShortUrl(String originalUrl) {
        URL url = validator.getValidUrl(originalUrl);
      // String hash = hashGenerator.getHash();
      // return url.getProtocol() + "://" + url.getHost() + "/" + hash;

        return "";
    }

    public String returnFullUrl(String shortUrl) {
        URL url = validator.getValidUrl(shortUrl);

        UrlAssociation urlAssociation = urlRepository.findById(url.getPath()).orElseThrow(
                () -> new IllegalStateException("For the specified hash the full URL is not in the database"));

        return urlAssociation.getUrl();
    }
}
