package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
	private final UrlRepository urlRepository;
	private final HashCache hashCache;
	private final UrlCacheRepository urlCacheRepository;

	@Value("${app.base-url:https://sh.com/}")
	private String baseUrl;

	@Override
	public UrlResponseDto createShortUrl(String longUrl) {
		String hash = hashCache.getHash();

		if (hash == null) {
			throw new IllegalStateException("No available hash in cache. Please generate more hashes.");
		}

		urlRepository.save(new Url(hash, longUrl));
		urlCacheRepository.saveUrlToRedis(hash, longUrl);
		String shortUrl = baseUrl + hash;

		return new UrlResponseDto(shortUrl);
	}

	@Override
	public String getOriginalUrl(String hash) {
		String url = urlCacheRepository.findUrlByHash(hash);
		if (url != null) {
			return url;
		}
		return urlRepository.findById(hash)
				.map(Url::getUrl)
				.map(originalUrl -> {
					urlCacheRepository.saveUrlToRedis(hash, originalUrl);
					return originalUrl;
				})
				.orElseThrow(() -> new UrlNotFoundException(hash));
	}
}