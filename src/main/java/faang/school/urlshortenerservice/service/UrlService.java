package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.URLDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    public String createShortLink(URLDto urlDto){
        return null;
    }
}
