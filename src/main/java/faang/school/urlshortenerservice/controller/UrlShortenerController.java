package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import faang.school.urlshortenerservice.utilites.UrlUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.URL)
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @Value("${hash.test-url.url-name}")
    private String urlName;

    @Value("${hash.hash.max-hash-length}")
    private int maxHashLength;

    private String regex;

    @PostConstruct
    public void init() {
        regex = "^" + urlName + "([^/]+)$";
        log.info("Post Construct init regex: {} ", regex);
    }

    @PostMapping()
    public UrlDto getShortUrl(@Valid @RequestBody UrlDto longUrl) {
        log.info("Long url: {}", longUrl.url());
        isValidLongUrl(longUrl.url());
        return urlShortenerService.getShortUrl(longUrl.url());
    }

    @GetMapping()
    public UrlDto getLongUrl(@Valid @RequestBody UrlDto shortUrl) {
        log.info("Short url : {}", shortUrl.url());
        Matcher matcher = isValidShortUrl(shortUrl.url());
        String hash = isValidHash(matcher);
        return urlShortenerService.getLongUrl(hash);
    }

    @GetMapping(UrlUtils.HASH)
    public ResponseEntity<Void> getLongUrlByHash(@PathVariable("hash") String hash) {
        log.info("Income hash : {}", hash);
        isValidHashLength(hash);
        String url = urlShortenerService.getLongUrl(hash).url();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }

    private void isValidLongUrl(String longUrl) {
        try {
            new URL(longUrl).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Url: {}  Validation Exception", longUrl);
            throw new ValidationException("Url Validation Exception");
        }
    }

    private Matcher isValidShortUrl(String shortUrl) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(shortUrl);

        if (!matcher.matches()) {
            log.error("Url has incorrect name {}, expected name {}", shortUrl, urlName);
            throw new ValidationException(String.format("Url has incorrect name %s, expected %s ", shortUrl, urlName));
        }
        return matcher;
    }

    private String isValidHash(Matcher matcher) {
        String hash = matcher.group(1);
        isValidHashLength(hash);
        return hash;
    }

    private void isValidHashLength(String hash) {
        if (hash.length() <= 0 || hash.length() > maxHashLength) {
            log.error("Url has incorrect hash {}", hash);
            throw new ValidationException(String.format("Url has incorrect hash %s", hash));
        }
    }
}