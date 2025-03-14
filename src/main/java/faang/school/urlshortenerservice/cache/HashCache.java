package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class HashCache {

    private final Queue<String> hashQueue = new LinkedBlockingQueue<>();

    @Value("${hash.cache.size}")
    private int hashBatchSize;

    public ExecutorService cache() {
        List<String> cache = new CopyOnWriteArrayList<>();

    }
}
