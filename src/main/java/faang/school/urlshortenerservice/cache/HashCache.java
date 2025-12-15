package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
public class HashCache {

	private final HashGenerator hashGenerator;
	private final HashRepository hashRepository;
	private final @Qualifier("hashCacheExecutor") Executor hashCacheExecutor;

	@Value("${hash.cache.capacity:500}")
	private int capacity;
	@Value("${hash.cache.fill-percent:20}")
	private int fillPercent;
	@Value("${hash.cache.batch-size:1000}")
	private int batchSize;

	private Queue<String> hashes;
	private final AtomicBoolean isFilling = new AtomicBoolean(false);

	@PostConstruct
	public void init() {
		this.hashes = new ArrayBlockingQueue<>(capacity);
		fillCacheWithHashes();
		fillHashesAsync();
	}

	public String getHash() {
		int threshold = capacity * fillPercent / 100;
		if (hashes.size() < threshold
				&& isFilling.compareAndSet(false, true)) {
			fillHashesAsync();
		}
		return hashes.poll();
	}

	private void fillHashesAsync() {
		hashCacheExecutor.execute(() -> {
			try {
				fillCacheWithHashes();
				hashGenerator.generateBatch(batchSize);
			} finally {
				isFilling.set(false);
			}
		});
	}

	private void fillCacheWithHashes() {
		List<String> hashBatch = hashRepository.getHashBatch(batchSize);
		hashBatch.forEach(hash -> hashes.offer(hash));
	}
}