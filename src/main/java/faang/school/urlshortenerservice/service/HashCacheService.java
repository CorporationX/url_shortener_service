package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCacheService {

    // Простая симуляция, создания HashCacheService отдельная задача
    public String getHash() {
        return "1FG8d";
    }
}
