package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    public void testEncode() {
        Assertions.assertEquals("L",base62Encoder.getEncode(11l));
    }

    @Test
    public void testEncodingsList() {
        List<String> checkList = List.of("A","C","L");
        Assertions.assertEquals(checkList,base62Encoder.encode(List.of(0l,2l,11l)));
    }

    @Test
    public void testEncodingsEmptyList() {
        Assertions.assertEquals(0,base62Encoder.encode(new ArrayList<>()).size());
    }
}