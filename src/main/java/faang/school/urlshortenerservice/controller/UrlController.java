package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("GET /{hash}")
    public void getHash(@PathVariable String hash){
        urlService.searchUrl(hash);
    }
}
