package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.property.UrlShortenerProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final Base62Encoder encoder;
    private final HashRepository hashRepository;
    private final UrlShortenerProperties properties;

    @Transactional
    public void generateBatch() {
        List<Long> generatedSeries = hashRepository.getUniqueNumbers(properties.getBatchSizeMax());
        List<Hash> hashes = encoder.encode(generatedSeries).stream().map((hashCode) -> {
            Hash hash = new Hash();
            hash.setHash(hashCode);
            return hash;
        }).toList();
        hashRepository.saveAll(hashes);
    }
}
