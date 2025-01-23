package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    private List<Long> list;

    @BeforeEach
    public void setUp() {
        list = List.of(1L, 56_800_235_583L);
        String base62Alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        ReflectionTestUtils.setField(base62Encoder, "base62Alphabet", base62Alphabet);
    }

    @Test
    void testEncode() {
        List<Hash> hashes = base62Encoder.encode(list);

        assertEquals(2, hashes.size());
        assertEquals("1", hashes.get(0).getHash());
        assertEquals("zzzzzz", hashes.get(1).getHash());
    }
}
