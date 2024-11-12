package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.unique-num-count}")
    private int uniqueNumCount;

    @Async("threadPool")
    public void generate() {
        List<Long> numbers = hashRepository.getUniqueNumbers(uniqueNumCount);
        List<String> hashes = base62Encoder.encode(numbers);
        hashRepository.save(hashes);
    }
}
