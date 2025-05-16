package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.BaseEncoder;
import faang.school.urlshortenerservice.enity.FreeHash;
import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;
    private final BaseEncoder baseEncoder;

    @Transactional
    public List<String> getHashes() {
        return checkHashesCountAndGenerate(hashRepository.findAndDelete(hashProperties.getGet().getMax()));
    }

    private List<String> checkHashesCountAndGenerate(List<String> hashes) {
        if (hashes.size() < hashProperties.getGet().getMin()) {
            CompletableFuture.runAsync(() ->
                    saveAll(hashRepository.getSequences(hashProperties.getGenerate()).stream()
                    .map(baseEncoder::encode)
                    .map(FreeHash::new)
                    .toList())); //todo add executor
        }
        return hashes;
    }

    public void saveAll(List<FreeHash> freeHashes) {
        hashRepository.saveAll(freeHashes);
    }
}
