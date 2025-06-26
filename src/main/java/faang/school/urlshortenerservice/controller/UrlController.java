package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrlDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/urls")
@Validated
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<ResponseUrlDto> shorten(@Valid @RequestBody RequestUrlDto requestUrlDto) {
        ResponseUrlDto response = urlService.shorten(requestUrlDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<Void> redirect(
            @PathVariable
            @Pattern(regexp = "^[0-9A-Za-z]{6}$", message = "Invalid hash format")
            String hash) {
        ResponseUrlDto response = urlService.getOriginalUrl(hash);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, response.getOriginalUrl())
                .build();
    }
}