package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CleanerScheduler {

	private final HashRepository hashRepository;
	private final UrlRepository urlRepository;

	@Scheduled(cron = "${scheduler.cleaner.old-urls-hashes.cron:0 0 0 * * ?}")
	@Transactional
	public void cleanOldUrls() {
		List<String> freedHashes = urlRepository.deleteOldUrls();
		if (!freedHashes.isEmpty()) {
			List<Hash> hashEntities = freedHashes.stream()
					.map(hash -> {
						Hash hashEntity = new Hash();
						hashEntity.setHash(hash);
						return hashEntity;
					})
					.toList();
			hashRepository.saveAll(hashEntities);
		}
	}
}
