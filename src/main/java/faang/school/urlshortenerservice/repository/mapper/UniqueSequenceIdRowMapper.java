package faang.school.urlshortenerservice.repository.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Bulgakov
 */

public class UniqueSequenceIdRowMapper implements RowMapper<Long> {
    @Override
    public Long mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getLong("nextval");
    }
}
