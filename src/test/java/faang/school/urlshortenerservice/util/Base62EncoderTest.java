package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private List<Long> list;

    @BeforeEach
    public void setUp() {
        list = List.of(1L, 56_800_235_583L);
    }

    @Test
    void testEncode() {
        List<Hash> hashes = new Base62Encoder().encode(list);

        assertEquals(2, hashes.size());
        assertEquals("1", hashes.get(0).getHash());
        assertEquals("zzzzzz", hashes.get(1).getHash());
    }
}
