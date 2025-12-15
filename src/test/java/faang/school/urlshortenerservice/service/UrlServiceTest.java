package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {

	@InjectMocks
	private UrlServiceImpl urlService;

	@Mock
	private UrlRepository urlRepository;

	@Mock
	private HashCache hashCache;

	@Mock
	private UrlCacheRepository urlCacheRepository;

	@Test
	void createShortUrl_ShouldGenerateAndSaveUrl() throws Exception {
		String longUrl = "https://example.com";
		String hash = "abc123";
		String shortUrl = "https://sh.com/abc123";

		when(hashCache.getHash()).thenReturn(hash);
		when(urlRepository.save(any(Url.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Field baseUrlField = UrlServiceImpl.class.getDeclaredField("baseUrl");
		baseUrlField.setAccessible(true);
		baseUrlField.set(urlService, "https://sh.com/");

		UrlResponseDto result = urlService.createShortUrl(longUrl);

		assertEquals(shortUrl, result.shortUrl());
		verify(urlRepository).save(argThat(url ->
				"abc123".equals(url.getHash()) &&
						"https://example.com".equals(url.getUrl())
		));
		verify(urlCacheRepository).saveUrlToRedis(eq(hash), eq(longUrl));
	}

	@Test
	void getOriginalUrl_ShouldReturnFromCache_WhenExists() {
		String hash = "abc123";
		String url = "https://example.com";

		when(urlCacheRepository.findUrlByHash(hash)).thenReturn(url);

		String result = urlService.getOriginalUrl(hash);

		assertEquals(url, result);
		verify(urlRepository, never()).findById(any());
	}

	@Test
	void getOriginalUrl_ShouldReturnFromDb_WhenNotInCache() {
		String hash = "abc123";
		String url = "https://example.com";
		Url entity = new Url(hash, url);

		when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
		when(urlRepository.findById(hash)).thenReturn(Optional.of(entity));

		String result = urlService.getOriginalUrl(hash);

		assertEquals(url, result);
		verify(urlCacheRepository).saveUrlToRedis(eq(hash), eq(url));
	}

	@Test
	void getOriginalUrl_ShouldThrow_WhenNotFound() {
		String hash = "unknown";

		when(urlCacheRepository.findUrlByHash(hash)).thenReturn(null);
		when(urlRepository.findById(hash)).thenReturn(Optional.empty());

		assertThrows(UrlNotFoundException.class, () -> urlService.getOriginalUrl(hash));
	}
}