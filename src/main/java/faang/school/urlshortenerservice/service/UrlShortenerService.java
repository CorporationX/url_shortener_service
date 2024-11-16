package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public void cleanOldUrls() {

//        // Получение URL-адресов старше года
//        Список<Url> oldUrls = urlRepository.findOldUrls(); // Реализуй этот метод в URLRepository
//
//// Сохрани освобожденные хэши обратно в хэш-таблицу
//        for (Url url : oldUrls) {
//            hashRepository.save(url.getHash()); // Настроить в соответствии с твоей логикой получения хэшей
//            urlRepository.delete(url); // Удали старую ассоциацию URL.
//        }
    }

    public String getUrl(String hash) {

        return "String()";
    }
}
