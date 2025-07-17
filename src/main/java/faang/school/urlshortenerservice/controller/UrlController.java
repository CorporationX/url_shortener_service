package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.facade.UrlFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        return ResponseEntity.ok(response);
    }
}
