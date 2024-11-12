package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    @Value(value = "${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private long batchSize;

    public String generateBatch() {
        List<Long> nums = hashRepository.getUniqueNumbers(batchSize);

    }
}
