package urlshortenerservice.controller;

import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final static String HASH_NULL = "Hash can't be null";
    private final UrlService urlService;

    @Operation(summary = "Получить оригинальный URL по хэшу",
            responses = {
                    @ApiResponse(responseCode = "302", description = "Перенаправление на оригинальный URL"),
                    @ApiResponse(responseCode = "404", description = "URL не найден"),
                    @ApiResponse(responseCode = "400", description = "Некорректный хэш")
            })
    @GetMapping("/{hash}")
    public ResponseEntity<String> getOriginalUrl(@PathVariable @Valid @NotNull(message = HASH_NULL) String hash) {
        String originalUrl = urlService.getOriginalUrl(hash);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @Operation(summary = "Создать короткий URL",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Ссылка успешна создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат URL")
            })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UrlResponseDto createUrl(@RequestBody @Valid UrlRequestDto url) {
        return urlService.createShortUrl(url.getUrl());
    }
}
