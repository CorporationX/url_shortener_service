package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
