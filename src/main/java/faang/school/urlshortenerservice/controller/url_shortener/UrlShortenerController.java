package faang.school.urlshortenerservice.controller.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url_shortener.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlService urlService;

    @PostMapping()
    public String shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.shortenUrl(urlDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
        String redirectUrl = urlService.getOriginalUrl(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
