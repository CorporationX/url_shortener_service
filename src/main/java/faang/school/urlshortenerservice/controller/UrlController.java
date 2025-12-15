package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.service.UrlServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {
	private final UrlServiceImpl urlService;

	@PostMapping("/url")
	public UrlResponseDto createShortUrl(@RequestBody @Valid UrlRequestDto urlRequestDto) {
		return urlService.createShortUrl(urlRequestDto.url());
	}

	@GetMapping("/{hash}")
	public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String hash) {
		String originalUrl = urlService.getOriginalUrl(hash);
		return ResponseEntity
				.status(HttpStatus.FOUND)
				.location(URI.create(originalUrl))
				.build();
	}
}