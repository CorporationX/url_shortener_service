package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.amount}")
    private final int hashAmount;

    @PostConstruct
    @Transactional
    public void generateHashes() {
        // чтобы обработать самостоятельно ошибку от базы данных, может нужно оборачивать запросы в базу в
        // try/catch, ловить исключение от базы и перепрокидывать в своё исключение, чтобы его обработал наш ExceptionHandler?
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers();
        List<String> newHashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(newHashes);
    }

    @Transactional
    public List<String> getHashes() {
        List<String> hashes = hashRepository.getHashBatch();
        if (hashes.size() < hashAmount) {
            generateHashes();
            hashes.addAll(hashRepository.getHashBatch());
        }

        return hashes;
    }

    @Async("executorServiceBean")
    public CompletableFuture<List<String>> getHashesAsync() {
        return CompletableFuture.completedFuture(getHashes());
    }
}
