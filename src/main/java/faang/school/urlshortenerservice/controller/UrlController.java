package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("${url-shortener.api-version}/")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/create")
    public ShortUrlResponseDto createHashedUrl(@RequestParam("url") String url) {
        return urlService.createHashedUrl(url);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getRealUrlByHash(@PathVariable String hash) {
        String url = urlService.getRealUrlByHash(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


}
