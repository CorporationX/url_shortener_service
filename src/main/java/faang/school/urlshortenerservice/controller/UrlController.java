package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/url")
public class UrlController {
    private final UrlService urlService;


    @PostMapping
    public ShortUrlDto createShortUrl
            (@Valid @RequestBody UrlRequestDto requestDto, HttpServletRequest request) {
        log.debug("Creating a new URL - Started");
        String longUrl = requestDto.getUrl();
        String hash = urlService.createShortUrl(longUrl);
        String baseUrl = getBaseUrl(request);
        String shortUrl = baseUrl + "/redirect/" + hash;
        return new ShortUrlDto(shortUrl);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        return scheme + "://" + serverName + serverPort + contextPath + servletPath;
    }

    @GetMapping("/redirect/{hash}")
    public RedirectView redirectToLongUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrl(hash);
        return new RedirectView(longUrl);
    }
}
