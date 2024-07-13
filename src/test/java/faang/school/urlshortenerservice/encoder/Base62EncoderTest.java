package faang.school.urlshortenerservice.encoder;
import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {
    private Base62Encoder encoder = new Base62Encoder(
            "abcdefghijklmnopqrsttuvwxyz0123456789ABCDEFDHIJKLMNOPQRSTUVWXYZ", 62);

    @Test
    public void whenEncodeThenGetHash() {
        List<Long> numbers = List.of(1L);
        assertThat(encoder.encode(numbers)).containsExactly(new Hash("b"));
    }
}