package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/url")
    public UrlDto createShortUrl(@Valid @RequestBody UrlDto dto, HttpServletRequest request) {
        return urlService.createShortUrl(dto, request);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(@PathVariable
                                         @Size(min = 6, max = 6, message = "Размер хеша должен быть равен 6")
                                         String hash) {
        return urlService.findOriginalUrl(hash);
    }
}
