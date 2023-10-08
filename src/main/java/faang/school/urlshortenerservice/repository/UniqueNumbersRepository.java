package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UniqueNumbersRepository {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=url_shortener";
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";

    public Set<Long> getUniqueNumbers(int n) {
        Set<Long> uniqueNumbers = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT nextval('unique_numbers_seq')";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                for (int i = 0; i < n; i++) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            long uniqueNumber = resultSet.getLong(1);
                            uniqueNumbers.add(uniqueNumber);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }

        return uniqueNumbers;
    }
}
