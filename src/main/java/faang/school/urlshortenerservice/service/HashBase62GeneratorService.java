package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueNumberSequenceRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HashBase62GeneratorService extends HashGeneratorService {

    private final HashRepository hashRepository;
    private final UniqueNumberSequenceRepository numberSequenceRepository;
    private final Encoder<Long, Hash> encoder;

    public HashBase62GeneratorService(HashRepository hashRepository,
                                      UniqueNumberSequenceRepository numberSequenceRepository,
                                      @Qualifier("base62Encoder") Encoder<Long, Hash> encoder) {
        this.hashRepository = hashRepository;
        this.numberSequenceRepository = numberSequenceRepository;
        this.encoder = encoder;
    }

    @Override
    @Async("mainThreadPoolExecutor")
    public void generateFreeHashes() {
        List<Long> uniqueNumbers = numberSequenceRepository.getUniqueNumbers(batchSizeForGenerateFreeHashes);
        List<Hash> hashes = encoder.encode(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
