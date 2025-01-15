package faang.school.urlshortenerservice.controller.url_shortener;

import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url_shortener.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlService urlService;

    @PostMapping()
    public String shortenUrl(@RequestBody @Valid UrlDto urlDto) {
        return urlService.shortenUrl(urlDto);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{hash}")
    public void redirectToOriginalUrl(@NotBlank(message = "Hash can't be empty")
                                      @Length(max = 6, message = "Max length 6 characters")
                                      @PathVariable String hash, HttpServletResponse response) {
        String redirectUrl = urlService.getOriginalUrl(hash);
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }
}
