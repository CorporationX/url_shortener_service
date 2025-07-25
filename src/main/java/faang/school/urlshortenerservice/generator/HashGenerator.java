package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Generated;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Generated base62Generated;

    @Value("${hash.range:1000}")
    private int maxRange;
    @Transactional
    @Async("executorForBase62")
    @PostConstruct
    public void generatedHash() {
        List<String> stringList = base62Generated.encodeBase62(sequenceNextRange());
        List<Hash> hashList = stringList.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashList);
    }
    @Transactional
    public List<Long> sequenceNextRange(){
        return hashRepository.getUniqueNumbers(maxRange);
    }
}
