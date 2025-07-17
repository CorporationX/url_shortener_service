package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlValidator validator = new UrlValidator(new String[]{"http", "https"});
    private final UrlService urlService;

    @PostMapping("/url")
    public String uploadLong(@RequestBody LongUrlDto longUrlDto) {
        String longUrl = longUrlDto.getLongUrl();
        if (!validator.isValid(longUrl)) {
            throw new IllegalArgumentException("provided url is not valid");
        }
        return urlService.shortAndReturn(longUrl);
    }

    @GetMapping("/{hash}")
    public RedirectView redirectOriginal(@PathVariable String hash) {
        String originalUrl = urlService.findOriginal(hash);
        return new RedirectView(originalUrl);
    }
}
