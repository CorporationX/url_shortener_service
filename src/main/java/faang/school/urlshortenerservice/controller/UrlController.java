package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/url")
@RestController
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @PostMapping
    public ResponseEntity<UrlDto> generateShortUrl(
            @Parameter(description = "Original URL to be shortened")
            @RequestBody @Valid UrlDto urlDto) {

        Url urlEntity = urlMapper.toEntity(urlDto);
        Url processedUrl = urlService.generateShortUrl(urlEntity);
        UrlDto result = urlMapper.toDto(processedUrl);

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Void> getUrlByHash(
            @Parameter(description = "Hash of Original URL in service")
            @RequestParam String hash) {
        if (!StringUtils.hasText(hash)) {
            return ResponseEntity.notFound().build();
        }

        try {
            String originalUrl = urlService.getUrl(hash);

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (UrlNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
