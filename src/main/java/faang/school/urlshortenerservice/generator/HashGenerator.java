package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    @Value("${app.hash.batch_size:20000}")
    private int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    private final HashMapper hashMapper;

    @Transactional
    @Scheduled(cron = "${app.scheduling.hash.cron}")
    public void generateAndSaveHashes(){
        var numbers = hashRepository.getUniqueNumbers(batchSize);
        var hashes = encoder.encode(numbers);

        hashRepository.saveAll(hashes.stream()
                .map(hashMapper::toEntity)
                .toList());
    }

    @Async("taskExecutor")
    public CompletableFuture<List<String>> getHashesAsync(int batchHashSize){
        return CompletableFuture.supplyAsync(() -> getHashes(batchHashSize));
    }

    public List<String> getHashes(int batchHashSize){
        generateAndSaveHashes();
        return hashRepository.getAndDeleteHashes(batchHashSize);
    }
}