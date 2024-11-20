package faang.school.urlshortenerservice.config.executor.rejection;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class CustomCallerRunsPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        log.warn("High server load! The task queue is overloaded with maximum size of {}! "
                        + "Active tasks : {} , Total tasks submitted : {}",
                executor.getQueue().size(), executor.getActiveCount(), executor.getTaskCount());
        if (!executor.isShutdown()) {
            runnable.run();
        }
    }
}
