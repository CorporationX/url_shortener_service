package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @PostMapping
    public String createUrl(@RequestBody UrlDto urlDto){
        log.info("Received a request to create url {}", urlDto);

        Url url = urlService.buildUrl(urlDto);
        return urlService.createUrl(url).getHash();
    }

    @GetMapping("/hash/{hash}")
    public String getUrl(@PathVariable String hash){
        log.info("Received a request to get url {}", hash);

        return urlService.getUrl(hash).getUrl();
    }
}
