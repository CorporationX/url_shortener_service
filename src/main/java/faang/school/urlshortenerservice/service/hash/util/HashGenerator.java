package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.util.encode.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder encoder;

    @Value("${app.hash_generator.get_unique_number_size}")
    private int numberSize;

    @Async("hashGeneratorExecutorPool")
    public void generate() {
        List<Long> numbers = hashService.getUniqueNumber(numberSize);
        List<String> hashes = encoder.encode(numbers);
        hashService.saveAllBatch(hashes);
    }
}
