package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value(value = "${hash_generator.n}")
    private Long n;

    @Async(value = "customPool")
    @Transactional
    public void generateBatch(){
        List<Long> listLong = hashRepository.getUniqueNumbers(n);
        List<Hash> hashes = encoder.encode(listLong);
        hashRepository.saveAll(hashes);
        log.info("______________________Save n = {} new hashes________________________________________", n);
    }
}
