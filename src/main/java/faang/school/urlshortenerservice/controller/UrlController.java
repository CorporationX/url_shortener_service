package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlEncodeDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * <h2>Задание</h2>
 * <div>Создать POST /url эндпоинт, который будет принимать длинную ссылку в качестве тела запроса, а
 * возвращать короткую ссылку, которая редиректит пользователя на длинную при переходе.</div>
 * <div>Нужно реализовать весь путь: получение запроса, валидация переданного URL (что это вообще URL,
 * а не что-то левое или пустота), получение хэша из HashCache бина (он будет разрабатываться в отдельной
 * задаче — здесь просто используем его API, мокаем, если нужно), сохранение ассоциации хэша и URL в базу и
 * в Redis.</div>
 * <h2>Критерии приема</h2>
 * <li>Это POST /url эндпоинт, который принимает DTO в качестве тела запроса.</li>
 * <li>Есть валидация переданных данных в контроллере, что это вообще корректный URL.</li>
 * <li>Метод-обработчик запроса находится в классе UrlController. Класс UrlController — Spring bean с
 * соответствующими аннотациями.</li>
 * <li>UrlService получается url пользователя, обращается в HashCache за хэшом для него и сохраняет ассоциацию
 * хэши и url в БД и в Redis. UrlService — spring bean.</li>
 * <li>Для сохранения в БД используется UrlRepository, который сохраняет эти данные в таблицу url.
 * Это spring bean.</li>
 * <li>UrlCacheRepository сохраняет данные в Redis. Это spring bean.</li>
 * <li>Везде используются аннотации lombok.</li>
 * <li>Все spring-аннотации отражают роли бинов.</li>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping()
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectByHash(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(urlService.redirectByHash(hash));
        return redirectView;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public String encodeUrl(@Valid @RequestBody UrlEncodeDto urlDto) {
        return urlService.encodeUrl(urlDto);
    }
}
