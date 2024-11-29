package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortLinkHashRepositoryTest {

    @Mock
    ShortLinkHashRepository repository;

    @Test
    public void testGetUniqueNumbers() {
        List<Long> mockResult = List.of(123451L, 123452L, 123453L, 123454L, 123455L);
        int count = 5;
        String query = """
                SELECT nextval('short_link_hash_seq')
                FROM generate_series(1, :count)
                """;
        when(repository.getListSequences(count)).thenReturn(mockResult);

        List<Long> result = repository.getListSequences(count);
        assertEquals(mockResult, result);
        assertEquals(mockResult.size(), 5);
    }
}