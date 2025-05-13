package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {

    private static final String SHORT_URL_PATTERN = "https://shorter-x/";

    private final UrlRepository urlRepository;
    private final HashGenerator hashGenerator;
    private final HashCache hashCache;

    @Value("${url-manipulation-setting.months-to-clear-url}")
    private int monthsToClearUrl;

    public String createShortUrl(UrlDto urlDto) {
        String url = urlDto.longUrl();
        String hash = hashCache.getCache();
        urlRepository.save(createUrlAssociation(url, hash));
        return SHORT_URL_PATTERN.concat(hash);
    }

    @Transactional
    public void cleanUnusedAssociations() {
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(monthsToClearUrl);
        List<String> hashes = urlRepository.findAndDeleteByCreatedAtBefore(createdAt);
        log.debug("Url before {} created date cleared successfully", createdAt);
        hashGenerator.processAllHashes(hashes);
        log.info("Hashes saved on database successfully");
    }

    private Url createUrlAssociation(String url, String hash) {
        return Url.builder()
                .url(url)
                .hash(hash)
                .build();
    }
}
