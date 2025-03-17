package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping("/v1/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDto> createShortUrl(
            @RequestHeader("x-user-id") String userId,
            @Valid @RequestBody UrlRequestDto urlRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlService.createShortUrl(urlRequestDto, userId));
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getOriginalUrl(
            @PathVariable
            @Pattern(regexp = "^[a-zA-Z0-9]{1,6}$",
                    message = "Некорректный формат hash")
            String hash) {

        return ResponseEntity.status(HttpStatus.FOUND)
                .body(urlService.getUrlFromHash(hash));
    }
}
