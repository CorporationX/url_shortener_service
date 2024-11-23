package faang.school.urlshortenerservice.crypto;

import org.junit.jupiter.api.BeforeEach;

public class Base62EncoderTest {

    private BaseEncoder baseEncoder;

    @BeforeEach
    public void setUp() {
        baseEncoder = new Base62Encoder();
    }
}
