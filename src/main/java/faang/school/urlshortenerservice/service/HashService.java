package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashService {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    public void clean() {

        log.info("Clean hash urlRepository");
        List<Url> oldUrlHashes = urlRepository.deleteOldUrl(LocalDateTime.now().minusYears(1));

    }
}
