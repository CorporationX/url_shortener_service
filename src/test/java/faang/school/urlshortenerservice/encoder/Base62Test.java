package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62Test {
    @Test
    public void testEncodeIntToBase62() {
        assertEquals("b", Base62.base62(1));
        assertEquals("dnh", Base62.base62(12345));
    }
}