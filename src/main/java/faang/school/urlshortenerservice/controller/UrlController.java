package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;


    @PostMapping
    public ResponseEntity<Map<String, String>> createShortUrl
            (@Valid @RequestBody UrlRequestDto requestDto, HttpServletRequest request) {
        log.debug("Creating a new URL - Started");
        String longUrl = requestDto.getUrl();
        validateUrl(longUrl);
        String hash = urlService.createShortUrl(longUrl);
        String baseUrl = getBaseUrl(request);
        String shortUrl = baseUrl + "/redirect/" + hash;
        Map<String, String> response = Map.of("shortUrl", shortUrl);
        return ResponseEntity.ok(response);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        boolean isDefaultPort = (scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443);
        return scheme + "://" + serverName + (isDefaultPort ? "" : ":" + serverPort) + contextPath;
    }

    @GetMapping("/redirect/{hash}")
    public RedirectView redirectToLongUrl(@PathVariable String hash) {
        String longUrl = urlService.getLongUrl(hash);
        if (longUrl == null || longUrl.isEmpty()) {
            // todo: Если URL не найден, можно перенаправить на страницу ошибки или на главную
            return new RedirectView("/not-found");
        }
        return new RedirectView(longUrl);
    }

    private void validateUrl(String longUrl) {
        if (longUrl == null || longUrl.isEmpty()) {
            throw new DataValidationException("URL cannot be null or empty");
        }
        boolean isValid = false;
        String[] urlStart = new String[]{"http", "https"};
        for (String start : urlStart) {
            if (longUrl.startsWith(start + "://")) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new DataValidationException("Invalid URL format");
        }
    }
}
