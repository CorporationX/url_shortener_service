package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.config.JdbcProperties;
import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class HashRepositoryCustomImpl implements HashRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private final JdbcProperties properties;
    private final String insertQuery = "INSERT INTO hash (hash) VALUES (?)";

    @Override
    @Transactional
    public <S extends Hash> List<S> saveAll(Iterable<S> entities) {
        List<String> hashes = StreamSupport.stream(entities.spliterator(), false)
                .map(Hash::getHash)
                .collect(Collectors.toList());


        List<List<String>> batches = ListUtils.partition(hashes, properties.getBatchSize());
        batches.forEach(batch -> {
            jdbcTemplate.batchUpdate(insertQuery, batch, batch.size(),
                    (PreparedStatement ps, String hash) -> ps.setString(1, hash));
        });
        return (List<S>) entities;
    }
}
