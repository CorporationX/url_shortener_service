package faang.school.urlshortenerservice;

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
    private final Base62Encoder encoder;

    @Value("${spring.constants.seq_batch_size}")
    private int sequenceSize;

    @Async("taskExecutor")
    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(sequenceSize);
        List<String> newHashes = encoder.encode(uniqueNumbers);
        hashRepository.save(newHashes);
    }
}
