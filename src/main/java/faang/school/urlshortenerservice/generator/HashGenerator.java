package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class HashGenerator {
    @Value("${app.number_generator.batch_size}")
    private int batchNumberSize;

    @Value("${app.hash.batch_size}")
    private int batchHashSize;

    private HashRepository hashRepository;
    private Base62Encoder encoder;

    @Transactional
    @Async("taskExecutor")
    public List<String> generateBatch(){
        var numbers = hashRepository.getUniqueNumbers(batchNumberSize);

        var hashes = encoder.encode(numbers);

        return hashRepository.saveAll(hashes);
    }
    @Transactional
    public List<String> getHashes(int batchHashSize){
        return hashRepository.getHashBatch(batchHashSize);
    }
}