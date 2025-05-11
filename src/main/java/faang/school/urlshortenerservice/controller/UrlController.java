package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Контроллер для работы с URL'ами
 * <p>
 * Основные функции:
 * <ul>
 *     <li>{@link #redirect(String) редирект на длинную ссылку} для указанной короткой ссылки.</li>
 *     <li>{@link #createShortUrl(UrlDto) Создание хэша ссылки} для заданной ссылки.</li>
 * </ul>
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;

    /**
     * Редирект на ссылку по хэшу
     * @param hash Хэш ссылки
     * @return редирект на URL для хэша
     */
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable @NotBlank String hash) {
        String url = urlService.getUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    /**
     * Создает хэш для ссылки
     * @param urlDto Объект со ссылкой
     * @return Хэш ссылки
     */
    @PostMapping("/url")
    public ResponseEntity<String> createShortUrl(@RequestBody @Valid UrlDto urlDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlService.createShortUrl(urlDto));
    }
}
