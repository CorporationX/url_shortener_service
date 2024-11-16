package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.dto.url.UrlResponseDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponseDto createShortUrl(@RequestBody @Valid UrlRequestDto urlRequestDto) {
        return urlService.createShortUrl(urlRequestDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> getUrl(@PathVariable String hash) {
        UrlDto url = urlService.getUrl(hash);

        return new ResponseEntity<>(url.getUrl(), HttpStatusCode.valueOf(302));
    }
}
