package faang.school.urlshortenerservice.service;

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

    private final UrlRepository urlRepository;
    private final HashGenerator hashGenerator;

    @Value("${url-manipulation-setting.months-to-clear-url}")
    private int monthsToClearUrl;

    @Transactional
    public void cleanUnusedAssociations() {
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(monthsToClearUrl);
        List<String> hashes = urlRepository.findAndDeleteByCreatedAtBefore(createdAt);
        log.debug("Url before {} created date cleared successfully", createdAt);
        hashGenerator.processAllHashes(hashes);
        log.info("Hashes saved on database successfully");
    }
}
