package faang.school.urlshortenerservice.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

@Component
public class HashCache {
    private Queue<String> freeCaches;
    @Value("${hash.queue.size}")
    private int queueSize;
    private TaskExecutor queueTaskThreadPool;

    @PostConstruct
    public void init(){
        freeCaches = new ArrayBlockingQueue<>(queueSize);
    }
}
