package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final Base62Encoder base62Encoder;
    private final HashRepository hashRepository;

    public List<String> generateHashes(int count) {
        return base62Encoder.encodeBatch(getNextSequenceBatch(count));
    }

    private List<Long> getNextSequenceBatch(int count) {
        List<Long> sequence = hashRepository.getNextSequenceBatchValues(count);
        Collections.shuffle(sequence);
        return sequence;
    }
}