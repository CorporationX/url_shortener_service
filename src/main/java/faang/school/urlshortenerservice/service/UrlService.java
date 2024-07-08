package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    private final UrlCacheRepository urlCacheRepository;


}
