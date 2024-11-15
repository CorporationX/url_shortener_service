package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.downgoon.snowflake.Snowflake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SnowflakeEncoderTest {

    @Mock
    private Snowflake snowflake;

    @InjectMocks
    private SnowflakeEncoder snowflakeEncoder;

    @Test
    void testEncode() {
        long mockId = 123456789L;
        Hash correctResult = new Hash(Long.toString(mockId));
        when(snowflake.nextId()).thenReturn(mockId);

        Hash result = snowflakeEncoder.encode(mockId);

        assertNotNull(result);
        assertEquals(correctResult, result);
        verify(snowflake).nextId();
    }
}