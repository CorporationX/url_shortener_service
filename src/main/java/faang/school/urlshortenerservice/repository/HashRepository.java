package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final JdbcTemplate template;
    private final DataSource dataSource;
    @Value("${data.hash.table_name}")
    private String tableName;
    @Value("${data.hash.sequence_name}")
    private String sequence;

    public List<Long> getUniqueNumbers(int n) {
        String query = "SELECT nextval('" + sequence + "') FROM generate_series(1, ?)";
        return template.queryForList(query, Long.class, n);
    }

    public void batchSave(List<Hash> hashes) {
        String query = "INSERT INTO " + tableName + " (hash) VALUES (?)";

        template.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, hashes.get(i).getHash());
            }

            @Override
            public int getBatchSize() {
                return hashes.size();
            }
        });
    }

    public List<Hash> findAndDelete(long amount) {
        String query = "DELETE FROM " + tableName + " WHERE hash IN (" +
                "SELECT hash FROM " + tableName + " LIMIT ?) " +
                "RETURNING hash";

        List<Hash> deletedHashes = new ArrayList<>();

        try (Connection connection = dataSource.getConnection(); // Получаем соединение из DataSource
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, amount);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                deletedHashes.add(new Hash(resultSet.getString("hash")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return deletedHashes;
    }
}
