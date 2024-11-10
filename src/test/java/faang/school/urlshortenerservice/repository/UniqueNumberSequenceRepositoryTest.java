package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UniqueNumberSequenceRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UniqueNumberSequenceRepository uniqueNumberSequenceRepository;

    @Test
    public void testGetNextSequenceValues() {
        List<Long> mockResult = List.of(10001L, 10002L, 10003L, 10004L, 10005L);
        int count = 5;
        String query = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, :count)
                """;
        when(jdbcTemplate.queryForList(query, Long.class, count)).thenReturn(mockResult);

        List<Long> result = uniqueNumberSequenceRepository.getNextSequenceValues(count);

        assertEquals(mockResult, result);
    }
}