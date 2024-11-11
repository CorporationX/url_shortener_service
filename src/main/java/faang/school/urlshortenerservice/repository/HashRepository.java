package faang.school.urlshortenerservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class HashRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Integer> getUniqueNumbers(int n) {
        String query = "SELECT nextval('unique_number_seq') from generate_series(1,:n)";
        Map<String, Integer> namedParams = Collections.singletonMap("n", n);
        return namedParameterJdbcTemplate.queryForList(query, namedParams, Integer.class);
    }
}
