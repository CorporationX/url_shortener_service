package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Hash;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void testEncodeSuccessful() {
        List<Hash> hashes = base62Encoder.encode(List.of(1235L, 6545L, 99999999L));
        assertThat(hashes).hasSize(3);
        assertThat(hashes.get(0).getHash().length()).isLessThan(6);
        assertThat(hashes.get(1).getHash().length()).isLessThan(6);
        assertThat(hashes.get(2).getHash().length()).isLessThan(6);
    }
}