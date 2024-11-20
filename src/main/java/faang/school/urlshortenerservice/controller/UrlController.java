package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.properties.url.UrlProperties;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/urls")
public class UrlController {

    private final UrlService urlService;
    private final UrlProperties urlProperties;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String getShortUrl(@Valid @RequestBody LongUrlDto url) {
        return urlProperties.getUrlShort().getBaseUrl() + urlService.saveAndConvertLongUrl(url);
    }

    @GetMapping("/{hash}")
    public void redirect(@NotBlank @NotEmpty @Size(min = 1, max = 6) @PathVariable String hash,
                         HttpServletResponse response) {
        Optional<String> url = urlService.retrieveLongUrl(hash);
        if (url.isPresent()) {
            response.setStatus(HttpServletResponse.SC_FOUND);
            response.setHeader("Location", url.get());
        } else {
            log.debug("Url is not found! {} ", url);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found");
        }
    }
}
