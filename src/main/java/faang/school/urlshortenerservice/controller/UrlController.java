package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@Validated
public class UrlController {
    private final UrlService urlService;
    private final UrlValidator urlValidator;

    @PostMapping("/url")
    @ResponseStatus(HttpStatus.OK)
    public String add(@RequestBody String url) {
        urlValidator.validate(url);
        return urlService.add(url);
    }

    @GetMapping("/{hash}")
    @ResponseStatus(HttpStatus.FOUND)
    public RedirectView get(@PathVariable @NotBlank(message = "A hash cannot be blank")
                            @Length(min = 1, max = 6, message = "A hash must be between 1 and 6 characters long") String hash) {
        return new RedirectView(urlService.get(hash));
    }
}
