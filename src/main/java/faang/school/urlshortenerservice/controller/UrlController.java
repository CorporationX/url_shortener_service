package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.exception.DataValidationException;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlValidator urlValidator = new UrlValidator();
    private final UrlService urlService;

    @PostMapping
    public String shorten(@RequestBody UrlDto urlDto) {
        if (!urlValidator.isValid(urlDto.getUrl())) {
            throw new DataValidationException("url is invalid");
        }
        return urlService.shorten(urlDto);
    }
}
