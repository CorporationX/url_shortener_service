package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    List<Hash> hashList;
    List<Long> numberList = List.of(465L, 82898L);

    @Test
    public void encodeTest() {
        hashList = base62Encoder.encode(numberList);

        assertEquals("7V", hashList.get(0).getHash());
        assertEquals("LZ4", hashList.get(1).getHash());

    }
}
