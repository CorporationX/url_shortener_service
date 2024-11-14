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
public class UniqueIdRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UniqueIdRepository uniqueIdRepository;

    @Test
    public void testGetNextSequenceValues() {
        int count = 3;
        List<Long> mockIds = List.of(1L, 2L, 3L);
        String query = """
                SELECT nextval('unique_number_seq')
                FROM generate_series(1, :count)
                """;

        when(jdbcTemplate.queryForList(query, Long.class, count)).thenReturn(mockIds);

        List<Long> uniqueIds = uniqueIdRepository.getUniqueNumbers(count);

        assertEquals(count, uniqueIds.size());
        assertEquals(mockIds, uniqueIds);
    }
}
