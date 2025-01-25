package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repozitory.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    @Value("${spring.url.hash.butch.size}")
    private int batchSize;

    //TODO сделать реализацию генерации списка хэшей
    @Async
    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
        List<Hash> hashes = new ArrayList<>();
        //List<Hash> hashes = encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
