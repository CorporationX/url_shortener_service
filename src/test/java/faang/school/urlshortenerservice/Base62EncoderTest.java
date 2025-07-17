package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncodeSingleNumber() {
        long input = 222333233340L;
        String actual = String.valueOf(encoder.encode(List.of(input)));
        System.out.println("Полученный хеш: " + actual);
    }
}
// Тест для проверки генерации хеша из 7 символов