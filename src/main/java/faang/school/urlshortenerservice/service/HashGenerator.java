package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder base62Encoder;

    public void generateBatch() {
        List<Long> uniqueNumbers = hashService.getUniqueNumbers();
        List<String> hashes = base62Encoder.encodeList(uniqueNumbers);
        hashService.save(hashes);
    }
}
