package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/url")
public class UrlController {
    private static final Pattern BASE62_PATTERN = Pattern.compile("^[0-9A-Za-z]+$");

    @Value("${hash.length:6}")
    private int hashLength;

    private final UrlService urlService;

    @PostMapping
    public CompletableFuture<ShortUrlDto> createShortUrl
            (@Valid @RequestBody UrlRequestDto requestDto, HttpServletRequest request) {
        String longUrl = requestDto.getUrl();
        log.info("Creating a new short URL for {} - Started", longUrl);
        return urlService.createShortUrl(longUrl, request);
    }

    @GetMapping("/redirect/{hash}")
    public RedirectView redirectToLongUrl(@PathVariable String hash) {
        validateHash(hash);
        String longUrl = urlService.getLongUrl(hash);
        return new RedirectView(longUrl);
    }

    private void validateHash(String hash) {
        if (hash == null || hash.isBlank() || hash.length() > hashLength) {
            throw new DataValidationException("Incorrect hash length or blank");
        }
        if (!BASE62_PATTERN.matcher(hash).matches()) {
            throw new DataValidationException("Hash contains invalid characters");
        }
    }
}