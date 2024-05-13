package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.context.AsyncExecutor;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@RequiredArgsConstructor
public class HashGenerator extends AsyncExecutor {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch}")
    private int batchSize;

    @Transactional
    public List<Hash> generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers).stream()
                .map(Hash::new)
                .toList();

       return  hashRepository.saveAll(hashes);
    }


    @Async("executor")
    public List<Hash> generateBatchAsync(){
        return generateBatch();
    }

}
