package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${base.api-path}/urls")
@RequiredArgsConstructor
@Validated
public class UrlController {

    private final UrlService urlService;
    @Value("${base.default-domain}")
    private String defaultDomain;
    @Value("${base.default-port}")
    private String defaultPort;
    @Value("${base.api-path}")
    private String baseApiPath;

    @PostMapping("/shorten")
    public UrlResponseDto shortenUrl(@RequestBody @Valid UrlRequestDto dto) {

        String shortUrl = String.format("%s:%s%s/urls/%s",
                defaultDomain, defaultPort, baseApiPath, urlService.shortenUrl(dto).getShortUrl());
        return new UrlResponseDto(shortUrl);

    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable
                                                      @Pattern(regexp = "^[a-zA-Z0-9]{7}$", message = "Invalid hash format") String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(".location", urlService.getOriginalUrl(hash))
                .build();
    }
}
