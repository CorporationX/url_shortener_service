package faang.school.urlshortenerservice.contoller;

import faang.school.urlshortenerservice.model.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("api/v1/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public String createShortUrl(@RequestBody @Validated UrlDto dto) {
        return urlService.createShortUrl(dto);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView getOriginalUrl(@RequestBody @Validated UrlDto dto) {
        String originalUrl = urlService.getOriginalUrl(dto);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(originalUrl);
        return redirectView;
    }
}
