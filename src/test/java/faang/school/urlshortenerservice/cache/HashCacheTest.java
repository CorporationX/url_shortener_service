package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

	@Mock
	private HashRepository hashRepository;

	@Mock
	private HashGenerator hashGenerator;

	@Mock
	private Executor hashCacheExecutor;

	@InjectMocks
	private HashCache hashCache;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(hashCache, "capacity", 10);
		ReflectionTestUtils.setField(hashCache, "fillPercent", 50);
		ReflectionTestUtils.setField(hashCache, "batchSize", 1000);
	}

	@Test
	void getHash_ShouldReturnHash_FromCache() {
		when(hashRepository.getHashBatch(1000)).thenReturn(List.of("h1"));
		doNothing().when(hashGenerator).generateBatch(1000);

		hashCache.init();

		ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
		verify(hashCacheExecutor, timeout(1000)).execute(captor.capture());
		captor.getValue().run();

		assertEquals("h1", hashCache.getHash());
	}

	@Test
	void getHash_WhenBelowThreshold_ShouldRefill() {
		when(hashRepository.getHashBatch(1000))
				.thenReturn(List.of("h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", "h9", "h10"))
				.thenReturn(List.of("h11"));
		doNothing().when(hashGenerator).generateBatch(1000);

		hashCache.init();

		ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
		verify(hashCacheExecutor, timeout(1000)).execute(captor.capture());
		captor.getValue().run(); // h1..h10

		// Берём 7 штук → h1..h7 → осталось: h8, h9, h10
		for (int i = 0; i < 7; i++) {
			hashCache.getHash();
		}

		verify(hashCacheExecutor, times(2)).execute(captor.capture());
		captor.getValue().run(); // → h11 в кэш

		assertEquals("h8", hashCache.getHash());
		assertEquals("h9", hashCache.getHash());
		assertEquals("h10", hashCache.getHash());
		assertEquals("h11", hashCache.getHash());
	}
}