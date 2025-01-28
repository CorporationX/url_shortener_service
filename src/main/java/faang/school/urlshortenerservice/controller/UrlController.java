package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.OriginalUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UrlController {

    @Value("${short-url.domain-address}")
    private String domainAddress;

    private final UrlService urlService;

    @PostMapping("/urlShortener")
    public ShortUrlDto getShortUrl(@Valid @RequestBody OriginalUrlDto originalUrl) {
        Hash hash = urlService.saveUrlAssociation(originalUrl);
        return new ShortUrlDto(domainAddress + hash.getHash());
    }

    @GetMapping("/urlShortener/{hash}")
    public void redirect(@PathVariable String hash, HttpServletResponse httpServletResponse) throws IOException {
        String url = urlService.getUrlByHash(hash);
        if (url != null) {
            httpServletResponse.sendRedirect(url);
        } else {
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

    }

}
