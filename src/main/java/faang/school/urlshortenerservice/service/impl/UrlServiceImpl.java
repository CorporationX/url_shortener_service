package faang.school.urlshortenerservice.service.impl;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {

    @Override
    public UrlDto createUrlDto(UrlDto urlDto) {
        return new UrlDto();
    }
}
