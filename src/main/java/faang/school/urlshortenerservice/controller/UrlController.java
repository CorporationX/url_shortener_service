package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.dto.UrlResponse;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/url")
public class UrlController {

    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @PostMapping
    public ResponseEntity<UrlResponse> createUrl(@Valid @RequestBody UrlRequest urlRequest) {
        Url url = urlService.createUrlMapping(urlRequest.getUrl(), urlRequest.getExpireAt());
        UrlResponse urlResponse = urlMapper.toUrlResponse(url);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlResponse);
    }


}
