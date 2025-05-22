package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;
    private final BaseEncoder baseEncoder;

    public List<FreeHash> generate() {
        return hashRepository.getSequences(hashProperties.getGenerateCount()).stream()
                .map(baseEncoder::encode)
                .map(FreeHash::new)
                .toList();
    }
}
