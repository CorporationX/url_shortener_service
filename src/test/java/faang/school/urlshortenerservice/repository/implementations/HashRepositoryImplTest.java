package faang.school.urlshortenerservice.repository.implementations;

import faang.school.urlshortenerservice.config.app.HashConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashRepositoryImplTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private HashConfig hashConfig;

    @InjectMocks
    private HashRepositoryImpl hashRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(hashConfig.getBatchSize()).thenReturn(3);
    }

    @Test
    public void testGetUniqueNumbers() {
        List<Long> mockNumbers = Arrays.asList(1L, 2L, 3L);

        when(jdbcTemplate.query(
                eq("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)"),
                any(RowMapper.class),
                eq(3)
        )).thenReturn(mockNumbers);

        List<Long> result = hashRepository.getUniqueNumbers(3);

        assertEquals(3, result.size());
        assertEquals(mockNumbers, result);
        verify(jdbcTemplate).query(
                eq("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)"),
                any(RowMapper.class),
                eq(3)
        );
    }

    @Test
    public void testSave() {
        List<String> hashes = Arrays.asList("000001", "000002", "000003");

        hashRepository.save(hashes);

        verify(jdbcTemplate).batchUpdate(
                eq("INSERT INTO hash (hash) VALUES (?) ON CONFLICT (hash) DO NOTHING"),
                any(BatchPreparedStatementSetter.class)
        );
    }

    @Test
    public void testGetHashBatch() {
        List<String> mockHashes = Arrays.asList("000001", "000002");
        String sqlQuery = "DELETE FROM hash WHERE hash IN (" +
                "SELECT hash FROM hash ORDER BY RANDOM() LIMIT ?)" +
                " RETURNING hash";

        doAnswer(invocation -> {
            String sql = invocation.getArgument(0);
            RowMapper<String> rowMapper = invocation.getArgument(1);
            Object batchSize = invocation.getArgument(2);

            if (sql.equals(sqlQuery) && batchSize.equals(3)) {
                System.out.println("Returning mockHashes: " + mockHashes);
                return mockHashes;
            }
            return List.of();
        }).when(jdbcTemplate).query(
                any(String.class),
                any(RowMapper.class),
                any()
        );

        List<String> result = hashRepository.getHashBatch();

        assertEquals(2, result.size());
        assertEquals(mockHashes, result);
        verify(jdbcTemplate).query(
                eq(sqlQuery),
                any(RowMapper.class),
                eq(3)
        );
    }
}