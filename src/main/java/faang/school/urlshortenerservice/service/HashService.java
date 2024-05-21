package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class HashService {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    public HashService(HashRepository hashRepository, UrlRepository urlRepository) {
        this.hashRepository = hashRepository;
        this.urlRepository = urlRepository;
    }


    @Async("taskExecutor")
    public void cleanAsync() {
        log.info("Cleaning Asynchronously {}", Thread.currentThread());
        clean();
    }

    @Transactional
    public void clean() {
        List<Url> urls = urlRepository.cleanOldHashes();
        if (urls != null) {
            hashRepository.saveAll(urls.stream().map(url -> new Hash(url.getHash())).toList());
        }
    }

}
