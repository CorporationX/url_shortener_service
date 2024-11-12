package faang.school.urlshortenerservice.config.executor.rejection;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class CustomCallerRunsPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        log.info("We are experiencing high server loads due to the influx of visitors. Please remain calm."
                + " Your request may take a little longer than usual!");
        if (!executor.isShutdown()) {
            runnable.run();
        }
    }
}
