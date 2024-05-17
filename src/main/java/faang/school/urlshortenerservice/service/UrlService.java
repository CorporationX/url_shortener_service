package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;
}
