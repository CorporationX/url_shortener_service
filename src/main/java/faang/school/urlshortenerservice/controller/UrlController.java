package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;

import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shortener")
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortUrlDto createShortUrl(@Valid @RequestBody UrlRequestDto urlRequestDto) {
        log.info("Create url : {}", urlRequestDto);
        Url url = urlMapper.toEntity(urlRequestDto);
        return urlService.createShortUrl(url);
    }
}
