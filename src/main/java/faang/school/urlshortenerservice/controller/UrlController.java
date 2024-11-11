package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public void redirect(@PathVariable
                         @Length(min = 6, max = 6, message = "Hash must be exactly 6 characters long")
                         String hash,
                         HttpServletResponse response) throws IOException {
        String redirectUrl = urlService.redirectByHash(hash);
        response.addHeader(HttpHeaders.LOCATION, redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
