package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.annotation.Url;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/url/")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public void redirectByHash(@PathVariable @Length(min = 6, max = 6, message = "The hash length must be 6 characters") String hash,
                                  HttpServletResponse response) throws IOException {
        redirect(urlService.getLongUrlByHash(hash), response);
    }

    @PostMapping("/shortener")
    public void shortener(@RequestBody @Validated @NotNull @Url String url, HttpServletResponse response) throws IOException {
        redirect(urlService.generateHashForUrl(url), response);
    }

    private void redirect(String redirectUrl, HttpServletResponse response) throws IOException {
        response.addHeader(HttpHeaders.LOCATION, redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
