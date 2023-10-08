package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HashJpaRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=url_shortener";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";
    private final HashRepository projectJpaRepository;
    @Value("${batchSize}")
    int batchSize;

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

    public List<String> getHashBatch(int n) {
        List<String> randomHashes = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM hash WHERE id IN (SELECT id FROM hash ORDER BY random() LIMIT ?) RETURNING hash_value";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, n);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String hashValue = resultSet.getString("hash_value");
                        randomHashes.add(hashValue);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return randomHashes;
    }
}
