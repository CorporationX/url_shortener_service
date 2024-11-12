package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public RedirectView redirectToLongUrl(@PathVariable Hash hash) {
        String longUrl = urlService.getLongUrlByHash(hash);

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(longUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }
}
