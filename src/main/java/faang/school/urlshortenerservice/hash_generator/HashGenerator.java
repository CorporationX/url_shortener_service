package faang.school.urlshortenerservice.hash_generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.batch_size:1000}")
    private int batchSize;

    @Async("executorService")
    @Transactional
    public void generatedBatch(){
        List<Long> generatedNumbers =hashRepository.getUniqueNumbers(batchSize);
        List<String> strings = base62Encoder.encode(generatedNumbers);
        List<Hash> hashes = base62Encoder.encode(generatedNumbers).stream().map(string ->
                Hash.builder().hash(string).build()).collect(Collectors.toList());
        hashRepository.saveAll(hashes);
    }
}
