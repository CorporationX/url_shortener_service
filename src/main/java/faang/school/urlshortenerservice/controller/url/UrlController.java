package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.dto.url.RequestUrlBody;
import faang.school.urlshortenerservice.dto.url.ResponseUrlBody;
import faang.school.urlshortenerservice.service.url.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/urls")
public class UrlController {

    private final UrlService urlService;

    @GetMapping("/{hash}")
    public void redirectShortLinkToFull(@PathVariable("hash") @NotBlank String hash,
                                        HttpServletResponse response) throws IOException {
        String originalUrl = urlService.getFullRedirectionLink(hash);

        response.sendRedirect(originalUrl);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    }

    @PostMapping
    public ResponseUrlBody convertLinkToShort(@RequestBody @Valid RequestUrlBody requestBody) {
        return urlService.convertUrlToShort(requestBody);
    }
}
