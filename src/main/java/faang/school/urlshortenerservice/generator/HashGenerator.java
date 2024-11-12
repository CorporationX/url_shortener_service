package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final JdbcTemplate jdbcTemplate;

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
//        List<Long> hashes = hashRepository.getNextRange(range);

        List<Set<Long>> hashesLists = splitList(hashes, generateBatchExecutorSize);
//        List<CompletableFuture<Set<String>>> futures = new ArrayList<>();

        hashesLists.forEach(list -> {
            CompletableFuture<Set<String>> base62resultSet = CompletableFuture.supplyAsync(() -> {
                List<Long> hashes = hashRepository.getNextRange(range);
                String sql = "INSERT INTO hash (hash) VALUES (?)";
                Set<String> ttt = base62Encoder.encode(list.stream().toList());
                List<String>bbb = ttt.stream().toList();

                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, bbb.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return hashes.size();
                    }
                });
                return base62Encoder.encode(list.stream().toList());
            }, executor);
//            futures.add(base62resultSet);
        });

//        Set<String> finalSet = futures.stream()
//                .map(CompletableFuture::join)
//                .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
//
//        List<String> listt = finalSet.stream().toList();

//        String sql = "INSERT INTO hash (hash) VALUES (?)";

//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setString(1, listt.get(i));
//            }
//
//            @Override
//            public int getBatchSize() {
//                return hashes.size();
//            }
//        });

        Long end = System.currentTimeMillis();
        System.out.println("duration " + (end - start));
    }

    private static List<Set<Long>> splitList(List<Long> list, int partCount) {
        int totalSize = list.size();
        int baseSize = totalSize / partCount;
        int remainder = totalSize % partCount;
        List<Set<Long>> result = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < partCount; i++) {
            int currentPartSize = baseSize + (i == partCount - 1 ? remainder : 0);
            Set<Long> part = new HashSet<>(list.subList(idx, idx + currentPartSize));
            result.add(part);
            idx += currentPartSize;
        }
        return result;
    }

//    @PostConstruct
//    @Transactional
//    public void generateBatch() {
//        Long start = System.currentTimeMillis();
//        // TODO remove commemnts
//        Set<String> hashes = base62Encoder.encode(hashRepository.getNextRange(range));
//        System.out.println("--------------------");
//        List<Hash> hashesToSave = hashes.stream().map(num -> Hash.builder().hash(num).build()).toList();
//        System.out.println("+++++++++++++++++++++++");
//        List<String>hashesList = hashesToSave.stream().map(num -> num.getHash()).toList();
//
//        String sql = "INSERT INTO hash (hash) VALUES (?)";
//
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setString(1, hashesList.get(i));
//            }
//
//            @Override
//            public int getBatchSize() {
//                return hashes.size();
//            }
//        });
//
////        hashRepository.saveAll(hashesToSave);
//        Long end = System.currentTimeMillis();
//        System.out.println("duration " + (end - start));
//    }
}
