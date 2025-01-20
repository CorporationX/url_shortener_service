package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEncoderTest {
    private String characters;
    private List<Long> numbers;
    private List<Hash> expectedHashes;
    private BaseEncoder baseEncoder;

    @BeforeEach
    void setUp() {
        characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        numbers = new ArrayList<>(List.of(1L, 2L, 3L));
        expectedHashes = new ArrayList<>(
                List.of(Hash.builder().hash("1").build(),
                        Hash.builder().hash("2").build(),
                        Hash.builder().hash("3").build()));
        baseEncoder = new BaseEncoder(characters);
    }

    @Test
    void testEncodeListSuccess() {
        List<Hash> result = baseEncoder.encodeList(numbers);
        assertThat(result).hasSize(numbers.size());

        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i)).isEqualTo(expectedHashes.get(i));
        }
    }
}