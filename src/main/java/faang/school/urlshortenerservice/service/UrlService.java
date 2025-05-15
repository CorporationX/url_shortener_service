package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashCashe hashCashe;
    private final UrlRepository urlRepository;
    private final UrlCasheRepository urlCasheRepository;
}
