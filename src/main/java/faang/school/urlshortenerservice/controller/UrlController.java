package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("api/v1/url")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectToOriginalUrl(
            @PathVariable @Length(min = 6, max = 6, message = "Hash must be exactly 6 characters long") String hash) {

        log.info("Redirect request for hash: {}", hash);

        String originalUrl = urlService.getOriginalUrl(hash);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);

        log.info("Redirecting to: {}", originalUrl);
        return redirectView;
    }
}
