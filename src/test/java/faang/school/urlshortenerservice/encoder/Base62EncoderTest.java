package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.validator.Base62Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder encoder;
    @Mock
    private Base62Validator validator;

    private List<Long> list = new ArrayList<>();

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(encoder, "base62Chars", "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        ReflectionTestUtils.setField(encoder, "base62Length", 62);
    }
    @Test
    void correctEncoderWork(){
        list.add(2L);

        List<String> result = encoder.encode(list);

        assertFalse(result.isEmpty());
        assertEquals(result.get(0),"2");
    }

    @Test
    void gettingEmptyList(){
        list.add(-1L);
        list.add(-12L);

        List<String> result = encoder.encode(list);
        assertTrue(result.isEmpty());
    }
}