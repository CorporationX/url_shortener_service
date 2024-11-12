package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.url.RequestUrlBody;
import faang.school.urlshortenerservice.dto.url.ResponseUrlBody;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/urls")
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public String redirectShortLinkToFull(@PathVariable("hash") @NotBlank String hash) {
        return urlService.getFullRedirectionLink(hash);
    }

    @PostMapping
    public ResponseUrlBody convertLinkToShort(@RequestBody @Valid RequestUrlBody requestBody) {
        return urlService.convertUrlToShort(requestBody);
    }
}
