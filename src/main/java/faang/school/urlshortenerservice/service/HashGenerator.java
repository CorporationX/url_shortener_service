package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    public void generateBatch() {
        //получить n уникальных чисел из БД
        List<Long> numbers = hashRepository.getUniqueNumbers();

        //отдаёт полученный список чисел в Base62Encoder, который возвращает уже список хэшей
        List<String> hashes = encoder.encode(numbers);

        //список этих хэшей сохраняет в БД через HashRepository в таблицу hash
    }
}
