package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    public void testEncode() {
        Assertions.assertEquals("A",base62Encoder.encode(10L));
    }
    @Test
    public void testEncodeList() {
        List<String> checkList = List.of("A","B");
        Assertions.assertEquals(checkList,base62Encoder.encode(List.of(10L,11L)));
    }
    @Test
    public void testEncodeEmptyList() {
        Assertions.assertEquals(0,base62Encoder.encode(new ArrayList<>()).size());
    }
}
