package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashGenerator;
import faang.school.urlshortenerservice.repository.UniqueldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UniqueldRepository uniqueldRepository;
    private final HashGenerator hashGenerator;

    public String generateUrl(URL){
        uniqueldRepository.findOne;
    }
}
