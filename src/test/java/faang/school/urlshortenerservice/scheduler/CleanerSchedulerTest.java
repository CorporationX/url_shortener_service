package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {

	@Mock
	private UrlRepository urlRepository;

	@Mock
	private HashRepository hashRepository;

	@InjectMocks
	private CleanerScheduler cleanerScheduler;

	@Test
	void cleanOldUrls_ShouldDeleteOldUrlsAndSaveHashes() {
		List<String> freedHashes = List.of("h1", "h2");
		when(urlRepository.deleteOldUrls()).thenReturn(freedHashes);

		cleanerScheduler.cleanOldUrls();

		verify(urlRepository).deleteOldUrls();
		verify(hashRepository).saveAll(anyList());
	}

	@Test
	void cleanOldUrls_ShouldNotSaveWhenNoHashes() {
		when(urlRepository.deleteOldUrls()).thenReturn(List.of());

		cleanerScheduler.cleanOldUrls();

		verify(urlRepository).deleteOldUrls();
		verify(hashRepository, never()).saveAll(anyList());
	}
}