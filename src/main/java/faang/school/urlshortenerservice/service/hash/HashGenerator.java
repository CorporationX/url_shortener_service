package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.encoder.Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Encoder encoder;

    @Async("hashGeneratorThreadPool")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateBatch() {
        log.info("Generate hashes for batch");

        List<Hash> hashes = encoder.encodeList(hashService.getNewNumbers())
                .stream()
                .map(Hash::new)
                .toList();

        log.info("Generated {} hashes for batch", hashes.size());
        hashService.saveHashes(hashes);
    }
}
