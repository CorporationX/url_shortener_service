package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCache {
    private final Executor hashExecutor;
    private Queue<String> hashQueue = new ArrayBlockingQueue<>(2);

    double percent = 0.2;
    int maxCapacity = 100;
    public String getHash() {
        if (hashQueue.size() > percent* maxCapacity) {
            return hashQueue.poll();
        } else {
//            hashExecutor.execute(); тут должен быть метод из hashRepository, но это отдельная задача. Тогда возбму её, потом вернусь к этой.
        }
        return "Заглушка";
    }

    private synchronized void fillHashQueue() {
        // вызов метода hashRepository;
    }
}
