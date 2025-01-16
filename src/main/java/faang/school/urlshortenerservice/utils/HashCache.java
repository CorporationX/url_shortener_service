package faang.school.urlshortenerservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCache {

    @Value("${hashCache.maxSize}")
    private int maxSize;

    @Value("${hashCache.lowThreshold}")
    private int lowThreshold;

    @Value("${hashCache.fillPercentage}")
    private double fillPercentage;


}
