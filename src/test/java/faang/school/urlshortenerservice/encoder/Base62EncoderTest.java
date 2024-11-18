package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder encoder;

    private List<Long> numbers;
    private List<String> expectedHashes;

    @BeforeEach
    public void setup() {
        numbers = new ArrayList<>(
            Arrays.asList(
                1L,
                2_141_213L,
                3_212_141_213L
            )
        );
        expectedHashes = new ArrayList<>(
            Arrays.asList(
                "1",
                "H1Z8",
                "fFOnv3"
            )
        );

    }


    @Test
    public void testEncode() {
        // Act
        List<String> receivedHashes = encoder.encode(numbers);

        // Assert
        assertEquals(expectedHashes, receivedHashes);
    }
}
