package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.OriginalUrlRequest;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/{hash}")
    public String redirectByHash(@PathVariable String hash) {
        return "redirect:" + urlService.getUrlByHash(hash);
    }

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String createShortUrl(@Valid @RequestBody OriginalUrlRequest request) {
        return urlService.createShortUrl(request);
    }
}
