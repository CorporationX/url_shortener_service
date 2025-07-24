package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    public ResponseEntity<ResponseUrlDto> createShortUrl(@Valid @RequestBody RequestUrlDto requestUrlDto) {
        ResponseUrlDto responseUrlDto = urlService.getShortenedUrl(requestUrlDto);
        return ResponseEntity.ok(responseUrlDto);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public ModelAndView redirectToOriginalUrl(@PathVariable String hash) {
        return new ModelAndView(String.format("redirect:%s", urlService.getLongUrlByHash(hash)));
    }
}
