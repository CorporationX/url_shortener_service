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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UrlControllerTest {
    private final UrlService urlService;

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
    public ResponseEntity<RedisUrl> getUrlFromHash(@PathVariable String hash){
        RedisUrl redisCashUrl = urlService.getCashUrl(hash);
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
