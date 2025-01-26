package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@ResponseBody
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService service;

    @PostMapping
    public String generateShortUrl(@RequestBody @Valid UrlDto originalUrl) {
        return service.generateShortUrl(originalUrl);
    }

    @GetMapping("/{hash}")
    public RedirectView returnFullUrl(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView(service.returnFullUrl(hash));
        redirectView.setStatusCode(HttpStatus.FOUND);

        return redirectView;
    }
}
