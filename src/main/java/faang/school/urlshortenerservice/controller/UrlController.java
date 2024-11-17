package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.annotaiton.ValidParams;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    @ValidParams
    public void postUrl(@RequestBody UrlDto dto) {
        urlService.saveAssociation(dto);
    }

    @GetMapping("/{hash}")
    public void getUrl(@PathVariable("hash") @Length(max = 6) String hash, HttpServletResponse response) {
        String originalUrl = urlService.getOriginalUrl(hash);
        try {
            response.setStatus(302);
            response.sendRedirect(originalUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
