package faang.school.urlshortenerservice.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HashCacheInitializer implements CommandLineRunner {

    private final HashCache hashCache;

    @Override
    public void run(String... args) throws Exception {
        hashCache.init();
    }
}