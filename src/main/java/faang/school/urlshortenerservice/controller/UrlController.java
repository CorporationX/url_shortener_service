package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.exception.InvalidUrlException;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UrlController {

  private final UrlService urlService;

  @PostMapping("/url")
  public UrlResponseDto addUrl(@Valid @RequestBody UrlCreateDto dto) {
    UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
    if (urlValidator.isValid(dto.url())) {
      return urlService.createShortUrl(dto);
    }
    throw new InvalidUrlException("Invalid URL: " + dto.url());
  }

  @GetMapping("/{hash}")
  public ResponseEntity<Void> redirect(@Valid @PathVariable("hash") String hash) {
    String url = urlService.getOriginalUrl(hash);
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(url)).build();
  }

}
