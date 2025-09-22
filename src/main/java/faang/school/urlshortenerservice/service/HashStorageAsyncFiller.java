package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HashStorageAsyncFiller {

    private final HashGenerator hashGenerator;

    @Async("hashGeneratorExecutor")
    public void refillStorageAsync(int count) {
        hashGenerator.refillHashStorage(count);
    }
}