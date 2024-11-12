package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("api/v1/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public void redirectByHash(@PathVariable
                         @Length(min = 6, max = 6, message = "Hash must be exactly 6 characters long")
                         String hash, HttpServletResponse response) throws IOException {
        redirect(urlService.redirectByHash(hash), response);
    }

    @PostMapping
    public void shortenUrl(@RequestBody @Validated UrlDto urlDto, HttpServletResponse response) throws IOException {
        redirect(urlService.shortenUrl(urlDto), response);
    }

    private void redirect(String redirectUrl, HttpServletResponse response) throws IOException {
        response.addHeader(HttpHeaders.LOCATION, redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
