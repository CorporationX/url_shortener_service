package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.facade.UrlFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
public class UrlController {
    private final UrlFacade urlFacade;

    @GetMapping("/{hash}")
    public ResponseEntity<String> getUrlByHash(@PathVariable String hash) {
        log.info("Url controller accepted request get url by hash {}", hash);

        String response = urlFacade.getUrlByHash(hash);
        log.info("Url controller return response get url by hash {}", response);

        return ResponseEntity.status(HttpStatus.FOUND).body(response);
    }

    @PostMapping
    public ResponseEntity<String> generateHash(@RequestBody @Valid UrlRequestDto urlRequestDto) {
        log.info("Url controller accepted request generate hash by url {}", urlRequestDto);

        String response = urlFacade.generateHash(urlRequestDto);
        log.info("Url controller return response generate hash by url {}", response);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
