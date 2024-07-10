package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Base62EncoderTest {

    @Autowired
    private Base62Encoder encoder;

    @Test
    public void whenEncodeThenGetHash() {
        List<Long> numbers = List.of(1L);
        assertThat(encoder.encode(numbers)).containsExactly(new Hash("b"));
    }
}