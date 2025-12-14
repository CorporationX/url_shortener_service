package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlResponse;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlController {

    private final UrlService urlService;

    /**
     * Создаёт короткую ссылку для переданного URL
     *
     * @param urlDto объект с длинным URL
     * @return короткая ссылка и хэш
     */
    @PostMapping
    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid @RequestBody UrlDto urlDto) {
        log.info("Received request to create short URL for: {}", urlDto.getUrl());

        ShortUrlResponse response = urlService.createShortUrl(urlDto.getUrl());

        log.info("Successfully created short URL: {}", response.getShortUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Перенаправляет на оригинальный URL по хэшу (редирект 302)
     *
     * @param hash хэш короткой ссылки
     * @return редирект на оригинальный URL
     */
    @GetMapping("/{hash}")
    public RedirectView redirectToOriginalUrl(@PathVariable String hash) {
        log.info("Received redirect request for hash: {}", hash);

        String originalUrl = urlService.getOriginalUrl(hash);

        log.info("Redirecting hash {} to: {}", hash, originalUrl);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}