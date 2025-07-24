package faang.school.urlshortenerservice.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.locks.ReentrantLock;

@UtilityClass
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
