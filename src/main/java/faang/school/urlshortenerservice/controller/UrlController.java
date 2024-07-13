package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/shortcuts")
@AllArgsConstructor
public class UrlController {
    private final UrlService urlService;

    @PostMapping("/url")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hash found successfully"),
            @ApiResponse(responseCode = "500", description = "Hash not exist")
    })
    public String convertToShortUrl(@RequestBody UrlDto urlDto) {
        return urlService.getHashFromUrl(urlDto);
    }

    @GetMapping("/{hash}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirect to original url"),
            @ApiResponse(responseCode = "404", description = "Url by hash not found")
    })
    public RedirectView redirectToUrlByHash(@PathVariable("hash") String hash) {
        String originalUrl = urlService.getUrlFromHash(hash);
        return new RedirectView(originalUrl);
    }
}