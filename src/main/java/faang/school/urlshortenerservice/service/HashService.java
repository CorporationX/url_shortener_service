package faang.school.urlshortenerservice.service;


import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {

    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;


    @Async
    public void cleanAsync(){
        log.info("Cleaning Asynchronously");
        clean();
    }

    @Transactional
    public void clean(){
        List<Url> urls = urlRepository.cleanOldHashes();
        if (urls != null){
            hashRepository.saveAll(urls.stream().map(url -> new Hash(url.getHash())).toList());
        }
    }

}
