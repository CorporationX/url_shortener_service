package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.generator.RedisHashCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UrlService {
    private final RedisHashCache hashCache;
}
