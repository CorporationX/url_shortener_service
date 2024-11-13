package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.annotation.logging.LogExecution;
import faang.school.urlshortenerservice.annotation.validation.ValidateBindingResult;
import faang.school.urlshortenerservice.dto.url.UrlRequestDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@RestController
public class UrlController {
    private final UrlService urlService;

    @SuppressWarnings("unused")
    @LogExecution
    @ValidateBindingResult
    @PostMapping("/url")
    public String createHashUrl(@RequestBody @Valid UrlRequestDto urlDto, BindingResult bindingResult) {
        return urlService.createHashUrl(urlDto.url());
    }

    @SuppressWarnings("deprecation")
    @ResponseStatus(HttpStatus.MOVED_TEMPORARILY)
    @GetMapping("/{hash}")
    public RedirectView redirect(@PathVariable String hash) {
        String primalUri = urlService.getPrimalUri(hash);
        return new RedirectView(primalUri);
    }
}
