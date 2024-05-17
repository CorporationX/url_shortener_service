package faang.school.urlshortenerservice.controller;

//import io.swagger.v3.oas.annotations.Operation; // TODO: swagger ПРЕКРУТИТЬ)
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("${app.url.endpoint.context-path}")
public class UrlController {

    private final UrlService urlService;

//    @Operation(summary = "Converts long url to short url") // TODO: swagger ПРЕКРУТИТЬ)
    @PostMapping("/url")
    public String convertToShortUrl(@RequestBody UrlDto urlDto) {
        return urlService.convertToShortUrl(urlDto);
    }

    @GetMapping("/{hash}")
//    @Operation(summary = "Finds original url from short url and redirects") // TODO: swagger ПРЕКРУТИТЬ)
    public RedirectView redirectOriginalUrl(@PathVariable String hash) {
        String url = urlService.redirectOriginalUrl(hash);
        return new RedirectView(url); // если я все правильно понял, то RedirectView автоматом возвращает
                                      // ответ со статусом 302 - HttpStatus.FOUND
    }
}
