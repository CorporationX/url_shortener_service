package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url-shortener")
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getUrl(@PathVariable(name = "hash") String hash) {
//        ProcessBuilder.Redirect
    }

}
//Задание
//
//UrlController должен содержать GET /{hash} эндпоинт, который перенаправит пользователя по оригинальной ссылке
// ассоциированной в БД с хэшом hash.
//
//Критерии приема
//
//UrlController — spring bean. Содержит обработчик запроса GET /{hash}. Возвращает ответ со статусом 302 и
// редиректом на длинный url, полученный из UrlService.
//
// UrlService, получив хэш, пробует найти соответствующий ему url сначала в Redis. Если там не удалось найти, то ищет в БД.
// Если и там нет, то кидает исключение.
//
//UrlCacheRepository используется для поиска url в Redis.
//
//UrlRepository используется для поиска url в БД в таблице url.
//
//Потенциальное исключение должно иметь соответствующий тип и сообщение.
//
//UrlController заворачивает полученный от UrlService url в http ответ со статусом 302 и редиректом по данной ссылке.
//
//Все классы — spring beans с соответствующими аннотациями.
//
//Везде используются аннотации lombok.