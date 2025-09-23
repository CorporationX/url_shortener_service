package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlViewDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Контроллер для работы с сокращением URL.
 * Обрабатывает HTTP-запросы на создание коротких ссылок и перенаправление.
 * </p>
 *
 * @author bozya
 * @since 18.09.2025
 */
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    /**
     * Выполняет перенаправление по короткой ссылке на оригинальный URL.
     *
     * @param hash хэш короткой ссылки
     * @return ответ с редиректом на оригинальный URL
     */
    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    /**
     * Создает короткую ссылку для указанного URL.
     *
     * @param dto DTO с оригинальным URL
     * @return DTO с созданной короткой ссылкой
     */
    @PostMapping("/createShortUrl")
    public ResponseEntity<UrlViewDto> createShortUrl(
            @Valid @RequestBody CreateShortUrlDto dto) {

        UrlViewDto result = urlService.createShortUrl(dto);
        return ResponseEntity.ok(result);
    }
}