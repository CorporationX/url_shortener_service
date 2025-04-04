package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${hash.batch-size}")
    private int batсhSize;

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    @Async("customThreadPool")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(batсhSize);
        List<Hash> hashes = numbers.stream()
                .map(number -> new Hash(base62Encoder.encode(number)))
                .toList();

        hashRepository.saveAll(hashes);
        log.debug("В базу данных добавлены сгенерированные хэши");
    }
}
