package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/url")
public class UrlController {
    private final UrlService urlService;

    @Operation(summary = "Create short url", description = "Creating short url and save in db")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public UrlDto openSavingAccount(@RequestBody @Validated(UrlDto.Create.class) UrlDto urlDto) {
        System.out.println(urlDto);
        return urlService.createUrlDto(urlDto);
    }
}
