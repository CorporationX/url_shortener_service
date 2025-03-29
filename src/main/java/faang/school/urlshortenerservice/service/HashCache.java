package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashCache {
    private final Base62Encoder base62Encoder;

    public List<String> getHashCache(List<Long> randomNumbersList) {
        return base62Encoder.encode(randomNumbersList);
    }
}
