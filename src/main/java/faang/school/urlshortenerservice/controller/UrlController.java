package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.RequestUrlBody;
import faang.school.urlshortenerservice.dto.ResponseUrlBody;
import faang.school.urlshortenerservice.service.urlService.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/urls/")
@RequiredArgsConstructor
@Valid
public class UrlController {

    private final UrlService urlService;

    @PostMapping
    public ResponseUrlBody convertLink(@RequestBody @Valid RequestUrlBody dto) {
        return urlService.convertLink(dto);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public String redirectLink(@PathVariable("hash") @NotBlank String hash) {
        return urlService.redirectLink(hash);
    }
}
