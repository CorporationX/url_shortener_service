package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.service.UrlUtilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/urls")
public class UrlShortenerController {

    private final UrlUtilService urlUtilService;
    private final UrlService urlService;

    @PostMapping("/create")
    public UrlResponseDto createShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        log.info("Received request: {}", urlRequestDto);
        return urlUtilService.shortenUrl(urlRequestDto);
    }

/*    @PostMapping("/get")
    UrlResponseDto getRedirectUrl(@Validated @RequestBody UrlRequestDto urlRequestDto) {
        log.info("Received request: {}", urlRequestDto);
        return urlUtilService.getFullUrl(urlRequestDto);
    }*/

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToUrl(@PathVariable String hash) {
        String fullUrl = urlService.getUrlByHash(hash).url();
        log.info("Redirection to URL: {}", fullUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", fullUrl)
                .build();
    }

}
