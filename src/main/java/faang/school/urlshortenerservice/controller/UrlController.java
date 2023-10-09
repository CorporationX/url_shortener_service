package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlValidator urlValidator = new UrlValidator();
    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String shorten(@RequestBody UrlDto urlDto) {
        if (!urlValidator.isValid(urlDto.getUrl())) {
            log.error("url is invalid");
            throw new DataValidationException("url is invalid");
        }
        return urlService.shorten(urlDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getUrl(@RequestParam String hash) {
        String url = urlService.getUrl(hash);
        log.info("redirect to {}", url);
        return new RedirectView(url);
    }
}
