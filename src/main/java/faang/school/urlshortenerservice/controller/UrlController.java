package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
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

import java.io.IOException;

@RestController
@RequestMapping("api/v1/shortener-service")
@RequiredArgsConstructor
public class UrlController {

    private final UrlShortenerService urlShortenerService;
    private final UrlMapper urlMapper;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping()
    public String shrinkUrl(@Valid @RequestBody UrlDto requestUrl) {
        return urlShortenerService.shrinkUrl(urlMapper.toEntity(requestUrl));
    }

    @GetMapping("/{hash}")
    public void redirectToOriginalUrl(@PathVariable(name = "hash") String hash,
                                      HttpServletResponse response) throws IOException {
        String originalUrl = urlShortenerService.getOriginalUrl(hash);

        response.setStatus(302);
        response.sendRedirect(originalUrl);
    }
}
