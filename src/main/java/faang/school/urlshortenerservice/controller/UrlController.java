package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUlrDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@RequestMapping(name = "/urls") //TODO перепроверить
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping(name = "/url")
    public ResponseEntity<ResponseUrlDto> shorten(@Valid @RequestBody RequestUlrDto requestUlrDto) {
        return ResponseEntity.ok(urlService.shorten(requestUlrDto));
    }

    @GetMapping(name = "{hash}")
    public ResponseEntity<ResponseUrlDto> getOriginalUrl(@PathVariable String hash) {
        return ResponseEntity.status(302).body(urlService.getOriginalUrl(hash));
    }
}
