package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder encoder;

    @Test
    public void testEncodeWithEmptyList() {
        List<String> result = encoder.encode(Collections.emptyList());
        assertTrue(result.isEmpty());
    }


    @Test
    public void testEncodeWithZero() {
        List<String> result = encoder.encode(List.of(0L));
        assertEquals("0", result.get(0));
    }


    @Test
    public void testEncodeWithMockedNumbers() {
        List<Long> mockedNumbers = List.of(42L, 1000L, 123456789L);
        Base62Encoder encoderSpy = spy(encoder);

        when(encoderSpy.encodeBase62(42L)).thenReturn("K");
        when(encoderSpy.encodeBase62(1000L)).thenReturn("g8");
        when(encoderSpy.encodeBase62(123456789L)).thenReturn("qIKF");

        List<String> result = encoderSpy.encode(mockedNumbers);

        verify(encoderSpy).encodeBase62(42L);
        verify(encoderSpy).encodeBase62(1000L);
        verify(encoderSpy).encodeBase62(123456789L);

        assertEquals("K", result.get(0));
        assertEquals("g8", result.get(1));
        assertEquals("qIKF", result.get(2));
    }
}