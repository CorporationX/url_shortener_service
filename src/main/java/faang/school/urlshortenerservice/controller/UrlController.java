package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.RedirectException;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "URL Controller", description = "API для работы с сокращенными URL")
public class UrlController {

    private final UrlService urlService;

    @SneakyThrows
    @PostMapping("/api/shorten")
    @Operation(summary = "Создать сокращенный URL", description = "Создает сокращенную версию переданного URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно созданный сокращенный URL"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат URL"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public String createShortUrl(
            @Parameter(description = "Оригинальный URL для сокращения", required = true) 
            @RequestParam String url) {
        return urlService.createShortUrl(url);
    }

    @GetMapping("/{shortUrl}")
    @Operation(summary = "Перенаправление по сокращенному URL", description = "Перенаправляет пользователя на оригинальный URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Успешное перенаправление"),
            @ApiResponse(responseCode = "404", description = "Сокращенный URL не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка при перенаправлении")
    })
    public void redirectToUrl(
            @Parameter(description = "Сокращенный URL", required = true) 
            @PathVariable String shortUrl, 
            HttpServletResponse response) {
        String originalUrl = urlService.getOriginalUrl(shortUrl);
        try {
            response.sendRedirect(originalUrl);
        } catch (IOException e) {
            log.error("Error sending redirect to {}", originalUrl, e);
            throw new RedirectException("Error sending redirect to " + originalUrl);
        }
    }
}