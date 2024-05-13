package faang.school.urlshortenerservice.config.context;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Callable;

public abstract class AsyncExecutor{
    @Async
    public <T> T executeAsync(Callable<T> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
