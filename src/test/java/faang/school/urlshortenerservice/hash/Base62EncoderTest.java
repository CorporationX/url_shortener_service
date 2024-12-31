package faang.school.urlshortenerservice.hash;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder encoder;

    @Test
    public void testEncode() {
        List<Long> longs = new ArrayList<>(List.of(986332332L));

        List<String> hashes = encoder.encode(longs);

        Assertions.assertEquals("14kY60", hashes.get(0));
    }
}