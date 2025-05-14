package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
import faang.school.urlshortenerservice.entity.RedisUrl;
import faang.school.urlshortenerservice.entity.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;


    /**
     * Links short URL to original URL
     * and saves this bind to Cash and to SQL DB
     * @param url - original URL that needs to have a short URL
     * @return - RedisUrl that keeps short and original URL
     */
    @PostMapping("/url/{url}")
    @ResponseBody
    public RedisUrl setShortUrl(@PathVariable String url){
        return urlService.setShortUrl(url);
    }

    @PostMapping("/url")
    @ResponseBody
    public RedisUrl setShortUrl(@Validated @RequestBody UrlDto urlDto) {
        return urlService.setShortUrl(urlDto.getUrl());
    }

    @GetMapping("/{hash}")
    public RedirectView getRedirectUrl(@PathVariable String hash) {
        RedirectView redirectView = urlService.getRedirectUrl(hash);;
        return redirectView;
    }

    @GetMapping("/nocache/{hash}")
    public RedirectView getRedirectUrlFromSQLDb(@PathVariable String hash) {
        RedirectView redirectView = urlService.getRedirectUrlFromSQLDb(hash);;
        return redirectView;
    }

    @PostMapping("/shorturl/cash")
    @ResponseBody
    public List<String> importShortUrlHashesToCash() throws InterruptedException {
        return urlService.importShortUrlHashesToQueueCash();
    }

    @PostMapping("/hash")
    @ResponseBody
    public String generateHash(){
        urlService.generateHashes();
        return "hashes were generated";
    }

    @GetMapping("/cashurl/{hash}")
    @ResponseBody
    public ResponseEntity<RedisCashUrl> getUrlFromHash(@PathVariable String hash){
        RedisCashUrl redisCashUrl = urlService.getUrlFromCash(hash);
        if(redisCashUrl == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(redisCashUrl);
    }

    @GetMapping("/hash/{number}")
    @ResponseBody
    public List<String> getHashesFromUrlTable(@PathVariable int number) {
        List<String> hashes = urlService.getHashesFromUrlTable(number);
        return hashes;
    }

    @GetMapping("/cash/size")
    @ResponseBody
    public long getCashSize() {
        return urlService.getCashQueueSize();
    }
}
