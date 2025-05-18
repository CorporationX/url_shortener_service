package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUlrDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@RequestMapping(name = "/urls") //TODO перепроверить
@Validated
public class UrlController {

    private final UrlService urlService;

    @PostMapping(name = "/url")
    public ResponseEntity<ResponseUrlDto> shorten(@Valid @RequestBody RequestUlrDto requestUlrDto) {
        urlService.shorten(requestUlrDto);
        return null;
    }

    @GetMapping(name = "{hash}")
    public ResponseEntity<ResponseUrlDto> h() {
        return null;
    }
}
