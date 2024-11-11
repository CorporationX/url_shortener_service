package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UrlController {
    private final UrlService urlService;

    @GetMapping("/method")
    public void method() {
        urlService.method();
    }
}
