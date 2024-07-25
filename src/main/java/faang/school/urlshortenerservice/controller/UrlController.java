package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("{hash}")
    void getRealUrl(@PathVariable String hash, HttpServletResponse response) {
        Url realUrl = urlService.getRealUrl(hash);
        response.setHeader("Location", realUrl.getUrl());
        response.setStatus(302);
    }
}
