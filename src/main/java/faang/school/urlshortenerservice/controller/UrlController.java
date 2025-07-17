package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
