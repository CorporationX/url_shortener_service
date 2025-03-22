package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.util.Base62Encoder;
import faang.school.urlshortenerservice.util.BaseEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @Mock
    private BaseEncoder baseEncoder;

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder(baseEncoder);
    }

    @Test
    void shouldEncodeValidInput() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L);
        when(baseEncoder.encode(1L)).thenReturn("a");
        when(baseEncoder.encode(2L)).thenReturn("b");
        when(baseEncoder.encode(3L)).thenReturn("c");

        List<String> result = base62Encoder.encode(numbers);

        assertEquals(Arrays.asList("a", "b", "c"), result);
        verify(baseEncoder, times(1)).encode(1L);
        verify(baseEncoder, times(1)).encode(2L);
        verify(baseEncoder, times(1)).encode(3L);
    }

    @Test
    void shouldThrowExceptionForEmptyList() {
        List<Long> emptyList = Collections.emptyList();

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                base62Encoder.encode(emptyList)
        );
        assertTrue(exception.getMessage().contains("Supplied list of numbers is empty!"));
        verify(baseEncoder, never()).encode(anyLong());
    }

    @Test
    void shouldThrowExceptionForDuplicateValues() {
        List<Long> duplicateList = Arrays.asList(1L, 2L, 1L);

        assertThrows(IllegalArgumentException.class, () ->
                base62Encoder.encode(duplicateList)
        );
        verify(baseEncoder, never()).encode(anyLong());
    }
}