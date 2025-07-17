package faang.school.urlshortenerservice.util;

import java.util.concurrent.locks.ReentrantLock;

public class LockUtil {
    public static void withLock(ReentrantLock lock, Runnable task) {
        if (lock.tryLock()) {
            try {
                task.run();
            } finally {
                lock.unlock();
            }
        }
    }
}
