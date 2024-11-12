package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.dto.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Autowired
    private ExecutorService executor;

    @Value("${hash.range:1000}")
    private int range;

    @Value("${thread.generate_batch_executor.size}")
    private int generateBatchExecutorSize;

    @PostConstruct
    @Transactional
    public void generateBatch() {
        Long start = System.currentTimeMillis();
        // TODO remove commemnts and remake hashesToSave try remove @OneToOne from Hash
        Set<String> hashes = base62Encoder.encode(hashRepository.getNextRange(range));
        List<String> hashesList = new ArrayList<>(hashes);

        List<List<String>> lists = splitList(hashesList, generateBatchExecutorSize);


        List<Hash> hashesToSave = hashes.stream().map(num -> Hash.builder().hash(num).build()).toList();
        hashRepository.saveAll(hashesToSave);
        Long end = System.currentTimeMillis();
        System.out.println("duration " + (end - start));
    }

    private static List<List<String>> splitList(List<String> list, int partCount) {
        long totalSize = list.size();
        long baseSize = totalSize / partCount;
        long remainder = totalSize % partCount;
        List<List<String>> result = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < partCount; i++) {
            long currentPartSize = baseSize + (i == partCount - 1 ? remainder : 0);
            List<String> part = list.subList(idx, idx + (int) currentPartSize);
            result.add(new ArrayList<>(part));
            idx += (int) currentPartSize;
        }
        return result;
    }
//    @PostConstruct
//    @Transactional
//    public void generateBatch() {
//        Long start = System.currentTimeMillis();
//        // TODO remove commemnts
//        Set<String> hashes = base62Encoder.encode(hashRepository.getNextRange(range));
//        List<Hash> hashesToSave = hashes.stream().map(num -> Hash.builder().hash(num).build()).toList();
//        hashRepository.saveAll(hashesToSave);
//        Long end = System.currentTimeMillis();
//        System.out.println("duration " + (end - start));
//    }
}
