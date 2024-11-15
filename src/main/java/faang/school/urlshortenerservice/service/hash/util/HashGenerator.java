package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.util.encode.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder encoder;

    @Value("${app.hash_generator.get_unique_number_size}")
    private int numberSize;

    @Async("hashGeneratorExecutorPool")
    public void generate() {
        Long dbHashesSeize = hashService.getHashesSize();
        if (dbHashesSeize < numberSize * 2L) {
            List<Long> numbers = hashService.getUniqueNumbers(numberSize);
            List<String> hashes = encoder.encode(numbers);
            hashService.saveAllBatch(hashes);
        }
    }
}
