package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class HashService {
    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Transactional
    public void clean(){
        log.info("Clean url repository");
        List<Url> cleaned = urlRepository.cleanOldUrl();
        hashRepository.saveAll(cleaned.stream().map(url ->
                new Hash(url.getHash())).toList());
    }
}
