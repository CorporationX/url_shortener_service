package faang.school.urlshortenerservice.cach;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.hash.HashGenerator;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {
    @Value("${queueCapacity}")
    private int queueCapacity;
    @Value("${hashCash.percent}")
    private float percent;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final BlockingQueue<Hash> blockingQueue;
    private final HashGenerator hashGenerator;


}
