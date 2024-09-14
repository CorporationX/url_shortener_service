package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    @Value("${hash.maxvalue: 10}")
    private int maxValue;

    @Transactional
    public void generateBatch() {
        //получить n уникальных чисел из БД
        List<Long> numbers = hashRepository.getUniqueNumbers(maxValue);

        //отдаёт полученный список чисел в Base62Encoder, который возвращает уже список хэшей
        List<Hash> hashes = encoder.encode(numbers);

        //список этих хэшей сохраняет в БД через HashRepository в таблицу hash
        hashRepository.saveAllAndFlush(hashes);
    }
}
