package faang.school.urlshortenerservice.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@AllArgsConstructor
@Slf4j
public class UrlJdbcRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=url_shortener";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";

    public List<String> deleteExpiredHashes() {
        List<String> expiredHashes = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM url WHERE created_at < NOW() - INTERVAL '1 year' RETURNING hash_value";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String hashValue = resultSet.getString("hash_value");
                        expiredHashes.add(hashValue);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return expiredHashes;
    }
}
