package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.entity.RedisUrl;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlService urlService;
       /**
     * Links short URL to original URL
     * and saves this bind to Cash and to SQL DB
     * @param urlDto - object that has Url in it
     * @return - RedisUrl that keeps short and original URL
     */
    @PostMapping("/url")
    @ResponseBody
    public RedisUrl setShortUrl(@Validated @RequestBody UrlDto urlDto) {
        return urlService.setShortUrl(urlDto.getUrl());
    }

    /**
     * Gets original Url from UrlShortenerService by its hash
     * @param hash - hash that is used to be associated with original Url
     * @return RedirectView that contains original redirect Url
     */
    @GetMapping("/{hash}")
    public RedirectView getRedirectUrl(@PathVariable String hash) {
        return urlService.getRedirectUrl(hash);
    }
}
