package faang.school.urlshortenerservice.service.url;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.hash.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Transactional
    public void method() {
//        hashGenerator.generateBatch();
        System.out.println(hashRepository.getHashBatch(10));
    }
}
