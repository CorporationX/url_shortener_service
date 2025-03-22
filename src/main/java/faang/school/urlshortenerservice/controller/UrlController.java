package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${url-shortener-service.api-version}")
public class UrlController {

  public static final String MATCH_URL =
      "[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

  private final UrlService urlService;

  @PostMapping("/url")
  public ResponseEntity<String> makeShortUrl(
      @RequestBody @Pattern(regexp = MATCH_URL) String longUrl) {
    String shortUrl = urlService.makeShortUrl(longUrl);

    return ResponseEntity.ok(shortUrl);
  }

  @GetMapping("/{hash}")
  public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
    String longUrl = urlService.getLongUrl(hash);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.LOCATION, longUrl);

    return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Found
  }
}



