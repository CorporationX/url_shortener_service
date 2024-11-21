package faang.school.urlshortenerservice.util.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Base62EncoderTest {
    private Base62Encoder encoder;
    private final Executor executor = Executors.newFixedThreadPool(10);

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder(executor);
        encoder.setHashSize(6);
    }

    @Test
    @DisplayName("Encoding batch of numbers")
    void Base62EncoderTest_encodeBatch() {
        List<Long> batch = getBatch();
        List<String> expected = getEncodedBatch();

        List<String> result = encoder.encodeBatch(batch);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Encoding empty batch")
    void Base62EncoderTest_encodeEmptyBatch() {
        List<Long> batch = new ArrayList<>();

        List<String> result = encoder.encodeBatch(batch);

        assertTrue(result.isEmpty());
    }

    private List<Long> getBatch() {
        return LongStream.range(1, 11)
                .boxed()
                .toList();
    }

    private List<String> getEncodedBatch() {
        return List.of(
                "100000",
                "200000",
                "300000",
                "400000",
                "500000",
                "600000",
                "700000",
                "800000",
                "900000",
                "A00000");
    }
}
