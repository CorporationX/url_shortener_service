package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("${base.url}/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public UrlDto createShortUrl(@RequestBody @Valid UrlDto dto, HttpServletRequest request) {
        String domain = String.format("%s://%s:%d/",
                request.getScheme(), request.getServerName(), request.getServerPort());
        return urlService.createShortUrl(dto, domain);
    }

    @GetMapping("/hash/{hash}")
    public ResponseEntity<UrlDto> getOriginalUrl(@PathVariable @NotBlank String hash) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlService.getOriginalUrl(hash).url()))
                .build();
    }
}
