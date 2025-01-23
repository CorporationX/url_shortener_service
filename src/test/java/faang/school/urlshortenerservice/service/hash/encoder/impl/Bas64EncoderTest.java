package faang.school.urlshortenerservice.service.hash.encoder.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Bas64EncoderTest {
    @InjectMocks
    private Base64Encoder encoder;

    @Test
    void testGetHash() {
        List<Long> numbers = List.of(9043L, 9876543210L);
        List<String> actual = encoder.encode(numbers, 6);
        List<String> expected = List.of("TNC", "qbBsMJ");
        assertEquals(expected, actual);
    }
}