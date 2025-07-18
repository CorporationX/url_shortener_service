package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrl;
import faang.school.urlshortenerservice.exception.InvalidUrlFormatException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.net.MalformedURLException;
import java.net.URL;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public String getShortUrl(@RequestBody @Valid LongUrl longUrl) {
        String originalUrl = longUrl.getUrl();
        try {
            URL parseUrl = new URL(originalUrl);
            StringBuilder baseUrlBuilder = new StringBuilder();
            baseUrlBuilder
                    .append(parseUrl.getProtocol())
                    .append("://")
                    .append(parseUrl.getHost());
            int port = parseUrl.getPort();
            if (port != -1 && port != parseUrl.getDefaultPort()) {
                baseUrlBuilder
                        .append(":")
                        .append(port);
            }
            String hash = urlService.getShortUrl(originalUrl);
            return baseUrlBuilder
                    .append("/")
                    .append(hash)
                    .toString();
        } catch (MalformedURLException e) {
            log.error("Invalid URL format: {}", originalUrl, e);
            throw new InvalidUrlFormatException();
        }
    }

    @GetMapping("/{hash}")
    public RedirectView getLongUrl(@PathVariable
                                   @Size(max = 6, message = "Hash must be 6 characters or less")
                                   String hash) {
        return new RedirectView(urlService.getLongUrl(hash));
    }
}
