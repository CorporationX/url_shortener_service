package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GetUniqueNumbersTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CustomHashRepository customHashRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUniqueNumbers() {
        int n = 5;
        List<Long> expectedNumbers = List.of(1L, 2L, 3L, 4L, 5L);

        when(jdbcTemplate.queryForList("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)", Long.class, n))
                .thenReturn(expectedNumbers);

        List<Long> actualNumbers = customHashRepository.getUniqueNumbers(n);

        assertEquals(expectedNumbers, actualNumbers);
        verify(jdbcTemplate, times(1)).
                queryForList("SELECT nextval('unique_number_seq') FROM generate_series(1, ?)", Long.class, n);
    }
}