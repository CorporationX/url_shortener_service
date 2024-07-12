package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final HashCache hashCache;
}
