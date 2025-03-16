package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${api-version}/")
@Validated
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public String createShortUrl(@RequestBody UrlDto url, HttpServletRequest request) {
        log.info("Create short url: {}", url);

        String builtUrl = buildUrl(urlService.createUrl(url.getUrl()), request);
        log.info("Built url: {}", builtUrl);
        return builtUrl;
    }

    private String buildUrl(Url url, HttpServletRequest request) {
        String host = request.getServerName();
        int port = request.getServerPort();
        return String.format("%s://%s:%d/%s", request.getScheme(), host, port, url.getHash());
    }
}
