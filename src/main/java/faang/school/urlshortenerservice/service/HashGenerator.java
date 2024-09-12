package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    public void generateBatch() {
        //получить n уникальных чисел из БД
        hashRepository.getUniqueNumbers();

        //отдаёт полученный список чисел в Base62Encoder, который возвращает уже список хэшей

        //список этих хэшей сохраняет в БД через HashRepository в таблицу hash
    }
}
