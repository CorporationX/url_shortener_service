package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlEncodeDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView redirectByHash(@PathVariable String hash) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(urlService.getUrlByHash(hash));
        return redirectView;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public String encodeUrl(@Valid @RequestBody UrlEncodeDto urlDto) {
        return urlService.encodeUrl(urlDto);
    }
}
