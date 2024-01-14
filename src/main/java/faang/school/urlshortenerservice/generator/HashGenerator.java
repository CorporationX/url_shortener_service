package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;

    @Value("${hash.maxRange:10000}")
    private int maxRange;
    @Transactional
    public void generateHash(){
        List<Long> range = hashRepository.getNextRange(maxRange);
    }
}
