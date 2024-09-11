package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class HashGenerator {
    @Value("${app.batch_size}")
    private int batchSize;

    private HashRepository hashRepository;
    private Base62Encoder encoder;

    @Transactional
    @Async("taskExecutor")
    public List<String> generateBatch(){
        var numbers = hashRepository.getNumbers(batchSize);

        var hashes = encoder.encode(numbers);

        return hashRepository.saveAll(hashes);
    }
}