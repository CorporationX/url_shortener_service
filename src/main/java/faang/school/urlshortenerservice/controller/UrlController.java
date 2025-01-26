package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.Cache;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/short")
@RequiredArgsConstructor
public class UrlController extends HttpServlet {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<?> putUrl(@RequestParam String url) {
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body("URL не может быть пустым");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalStateException("Некорректный формат URL: " + url);
        }

        try {
            Url saveUrl = urlService.putUrl(url);
            return ResponseEntity.ok(saveUrl);
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/{hash}")
    public void getHash(@PathVariable String hash, HttpServletResponse response) throws IOException {
        try {
            String originalUrl = urlService.getHash(hash);
            response.sendRedirect(originalUrl);

            if (originalUrl == null || originalUrl.isEmpty()) {
                throw new IllegalArgumentException("URL не найден для hash: " + hash);
            }

            if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                throw new IllegalStateException("Некорректный формат URL: " + originalUrl);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/getRedis")
    public ResponseEntity<Cache> getCache(@RequestParam String hash) {
        Cache cache = urlService.getFromCache(hash);
        return ResponseEntity.ok(cache);
    }
}

