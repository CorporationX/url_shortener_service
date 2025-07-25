package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.url.CreateUrlDto;
import faang.school.urlshortenerservice.dto.url.ResponseShortUrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlHashCacheService;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlHashCacheService urlHashCacheService;
    private final UrlService urlService;
    @GetMapping("/{hash}")
    public ResponseEntity<Void> getByHash(@RequestParam("hash") String hash) {
        String url = urlHashCacheService.getUrlByHash(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }
    @PostMapping
    public ResponseEntity<ResponseShortUrlDto> createShortUrl(@RequestBody CreateUrlDto createUrlDto){
        Url newUrl = urlService.createUrl(UrlMapper.urlCreateDtoToUrl(createUrlDto));
        return ResponseEntity.ok(new ResponseShortUrlDto(newUrl.getUrl(), newUrl.getCreatedAt()));
    }
}
