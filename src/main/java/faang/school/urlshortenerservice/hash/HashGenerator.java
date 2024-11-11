package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.beans.Encoder;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${hash.generated-batch-size}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62 base62;

    @Async("customThreadPool")
    public void generateBatch() {
        List<Integer> nums = hashRepository.getUniqueNumbers(batchSize);

        List<String> hashes = nums.stream()
                .map(val -> new String(base62.encode(val.toString().getBytes())))
                .toList();
        hashRepository.save(hashes);
    }
}
