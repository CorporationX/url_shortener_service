package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashJpaRepository {
    private final HashRepository projectJpaRepository;
    @Value("${batchSize}")
    private int batchSize;

    @Transactional
    public List<Hash> saveBatch(List<Hash> hashes) {
        List<Hash> result = new ArrayList<>(hashes.size());
        for (int i = 0; i < hashes.size(); i += batchSize) {
            int size = i + batchSize;
            if (size > hashes.size()) {
                size = hashes.size();
            }
            List<Hash> sub = hashes.subList(i, size);
            projectJpaRepository.saveAll(sub);
            result.addAll(sub);
        }
        return result;
    }
}
