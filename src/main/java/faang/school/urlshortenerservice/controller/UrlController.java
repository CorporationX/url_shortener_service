package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
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

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;

    @GetMapping("{hash}")
    public RedirectView getRedirectUrl(@PathVariable String hash) {
        RedirectView redirectView = urlService.getRedirectUrl(hash);;
        return redirectView;
    }

    @PostMapping("/url")
    @ResponseBody
    public String createShortUrl(@Validated @RequestBody UrlDto urlDto) {
        return "";
    }

    @PostMapping("/hash")
    @ResponseBody
    public String generateHash(){
        urlService.generateHashes();
        return "hashes were generated";
    }

    @PostMapping("/cashurl/{url}")
    @ResponseBody
    public RedisCashUrl saveUrlToCash(@PathVariable String url){
        RedisCashUrl redisCashUrl = new RedisCashUrl();
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(url);
        redisCashUrl.setUrlDto(urlDto);
        redisCashUrl.setHash(UUID.randomUUID().toString());
        return urlService.saveCashUrl(redisCashUrl);
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
}
