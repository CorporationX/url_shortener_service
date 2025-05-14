package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class UrlControllerV2 {
    private final UrlService urlService;

    @PostMapping("/cashurl/{url}")
    @ResponseBody
    public RedisUrl saveUrlToCash(@PathVariable String url){
        return urlService.setShortUrl(url);
    }

    @GetMapping("/cashurl/{hash}")
    @ResponseBody
    public ResponseEntity<RedisUrl> getUrlFromHash(@PathVariable String hash){
        RedisUrl redisUrl = urlService.getCashUrlV2(hash);
        if(redisUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(redisUrl);
    }

    @GetMapping("/cashurl/all")
    @ResponseBody
    public ResponseEntity<List<RedisUrl>> getAllUrlsFromHash(){
        ArrayList<RedisUrl> redisUrls  = urlService.getCashUrlAllV2();
        if(redisUrls == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(redisUrls);
    }

    @PostMapping("/shorturl/cash")
    @ResponseBody
    public List<String> importShortUrlHashesToCash() {
        return urlService.importShortUrlHashesToQueueCash();
    }

    @GetMapping("/url/{hash}")
    @ResponseBody
    public RedirectView getRedirectUrl(@PathVariable String hash) {
        return urlService.getRedirectUrl(hash);
    }
}
