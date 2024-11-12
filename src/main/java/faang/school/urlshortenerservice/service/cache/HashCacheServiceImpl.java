package faang.school.urlshortenerservice.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashCacheServiceImpl implements HashCacheService {

    @Override
    public String getHash() {
        return "ny3cgd";
    }
}
