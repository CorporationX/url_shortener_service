package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.RedisCashUrl;
import faang.school.urlshortenerservice.entity.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

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
    public String createShortUrl(@Validated @RequestBody UrlDto urlDto) {
        return "";
    }

    @PostMapping("/hash")
    public void generateHash(){
        urlService.generateHashes();
    }

    @PostMapping("/cashurl/{url}")
    public RedisCashUrl saveUrlToCash(@PathVariable String url){
        RedisCashUrl redisCashUrl = new RedisCashUrl();
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(url);
        redisCashUrl.setUrlDto(urlDto);
        return urlService.saveCashUrl(redisCashUrl);
    }
}
