package faang.school.urlshortenerservice.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncHashGenerator {
    private final TransactionalHash transactionalHash;

    @Async("hashGeneratorExecutor")
    public void generateBatchAsync() {
        log.info("Async hash generation started");
        transactionalHash.generateAndSaveHashes();
    }
}
