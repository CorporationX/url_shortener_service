package faang.school.urlshortenerservice.generator;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HashGenerator {

    @Value("${unique-numbers.size}")
    private long uniqueNumbers;

    private HashRepository hashRepository;

    private Base62Encoder base62Encoder;

    @Async("getThreadPool")
    public void generateBatch(long uniqueNumbers) {
        List<Long> numbersFromDB = hashRepository.getUniqueNumbers(uniqueNumbers);
        List<Hash> hashes = base62Encoder.encode(numbersFromDB);
        hashRepository.save(hashes);
    }
}
