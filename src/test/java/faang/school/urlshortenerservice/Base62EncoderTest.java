package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncodeSingleNumber() {

        long input = 222333233340L;
        String expectedHash = "3ugZaWa";

        String actualHash = encoder.encode(List.of(input)).get(0);
        System.out.println("Полученный хеш: " + actualHash);

        assertEquals(expectedHash, actualHash, "Закодированный хеш не соответствует ожидаемому");
    }
}