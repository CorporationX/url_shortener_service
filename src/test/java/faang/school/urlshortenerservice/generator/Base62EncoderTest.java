package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;
    String solWordTest;
    int solWordLengthTest;

    @BeforeEach
    public void setUp() {
        solWordTest = "a1b";
        ReflectionTestUtils.setField(base62Encoder, "solWord", solWordTest);
        base62Encoder.init();
    }

    @Test
    void initSuccessTest() {
        solWordLengthTest = (int) ReflectionTestUtils.getField(base62Encoder, "solWordLength");
        assertEquals(solWordTest.length(), solWordLengthTest, "Check solWordLength");
    }

    @Test
    void encodeListSuccessTest() {
        List<Long> longList = List.of(100L, 102L, 105L);
        List<String> hashList = List.of("1aba1", "a1ba1", "abba1");
        List<String> hashListResult = base62Encoder.encodeList(longList);

        assertEquals(3, hashListResult.size(), "Check size of hashList");
        hashList.forEach(hash -> assertTrue(hashListResult.contains(hash), "Check hashList contains encoded number"));
    }

    @Test
    void encodeNumberSuccessTest() {
        Long initialNumber = 100L;
        String hash = "1aba1";
        String hashListResult = base62Encoder.encodeNumber(initialNumber);
        assertEquals(hash, hashListResult, "Check encoded number");
    }

    @Test
    void encodeListNullExceptionSuccessTest() {
        List<String> hashListResult = base62Encoder.encodeList(null);
        assertEquals(0, hashListResult.size(), "Check null List");
    }
}