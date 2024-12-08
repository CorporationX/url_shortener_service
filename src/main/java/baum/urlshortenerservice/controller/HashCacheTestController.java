package baum.urlshortenerservice.controller;

import baum.urlshortenerservice.cache.HashCache;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/cache")
@RequiredArgsConstructor
public class HashCacheTestController {
    private final HashCache cache;

    @GetMapping("/getHash")
    public String getHash() {
        return cache.getHash();
    }
}
