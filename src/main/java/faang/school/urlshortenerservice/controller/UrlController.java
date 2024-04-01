package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URL;

@RestController
@Tag(name = "UrlShortener")
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;

    @Operation(
            summary = "Сделаем ссылку короче",
            description = "Загоняем длинную ссылку, получаем короткую"
    )
    @PostMapping
    public URL createShortUrl(@RequestBody @Valid String urlDto) {
        return urlService.createShortUrl(urlDto);
    }

    @Operation(
            summary = "Вернём длинную ссылку",
            description = "Загоняем короткую ссылку, получаем длинную"
    )
    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.PERMANENT_REDIRECT)
    public ResponseEntity<RedirectView> getLongUrl(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView(urlService.getLongUrl(hash));
        return ResponseEntity.status(302).body(redirectView);
    }
}