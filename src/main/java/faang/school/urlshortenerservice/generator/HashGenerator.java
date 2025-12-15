package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
	private final Base62Encoder base62Encoder;
	private final HashRepository hashRepository;

	@Async("hashGeneratorExecutor")
	public void generateBatch(int batchSize) {
		List<Long> numbers = hashRepository.getUniqueNumbers(batchSize);
		List<String> hashes = base62Encoder.encode(numbers);
		List<Hash> hashEntities = hashes.stream()
				.map(hash -> {
					Hash hashEntity = new Hash();
					hashEntity.setHash(hash);
					return hashEntity;
				})
				.toList();
		hashRepository.saveAll(hashEntities);
	}
}
