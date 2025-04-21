package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlResponseDto;
import faang.school.urlshortenerservice.dto.UrlRequestBodyDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    public ShortUrlResponseDto createHashedUrl(
            @Valid @RequestBody UrlRequestBodyDto requestBodyDto,
            HttpServletRequest request
    ) {
        return urlService.createShortUrl(requestBodyDto.getUrl(), request);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> getRealUrlByHash(@NotNull @NotBlank @PathVariable String hash) {
        String url = urlService.getRealUrlByHash(hash);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


}
