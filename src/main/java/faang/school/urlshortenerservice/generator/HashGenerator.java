package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utils.Base62Encoder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    public void generateAndSaveNewHashes(){
        hashRepository.saveAll(generateNewHashes(maxRange));
    }

    private List<Hash> generateNewHashes(int maxRange) {
        List<Long> uniqueNumbers = hashRepository.getNextRange(maxRange);
        return uniqueNumbers.stream()
                .map(Base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }
}
