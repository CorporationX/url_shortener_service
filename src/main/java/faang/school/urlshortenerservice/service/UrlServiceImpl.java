package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    @Override
    public String getOriginalUrl(String hash) {
        return null;
    }
}
