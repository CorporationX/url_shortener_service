package faang.school.urlshortenerservice.utils.hash;

import faang.school.urlshortenerservice.dto.hash.HashDto;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.hash.HashMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.utils.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashMapper hashMapper;

    @Value("${server.hash.generate.batch.size}")
    private int generateBatchSize;

    @Async("asyncThreadPoolExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(generateBatchSize);
        List<HashDto> hashDtoList = base62Encoder.encodeList(uniqueNumbers);
        List<Hash> hashList = hashMapper.toEntityList(hashDtoList);

        hashRepository.saveAll(hashList);
    }
}
