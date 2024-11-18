package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.LongStream;

@Primary
@Service
public class HashSnowflakeGeneratorService extends HashGeneratorService {

    private final HashRepository hashRepository;
    private final Encoder<Long, Hash> encoder;

    public HashSnowflakeGeneratorService(HashRepository hashRepository,
                                         @Qualifier("snowflakeEncoder") Encoder<Long, Hash> encoder) {
        this.encoder = encoder;
        this.hashRepository = hashRepository;
    }

    @Override
    @Async("mainThreadPoolExecutor")
    public void generateFreeHashes() {
        List<Hash> hashes = LongStream.range(0, batchSizeForGenerateFreeHashes)
                .mapToObj(encoder::encode)
                .toList();
        hashRepository.saveAll(hashes);
    }
}
