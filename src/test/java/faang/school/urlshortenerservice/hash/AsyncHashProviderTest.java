package faang.school.urlshortenerservice.hash;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncHashProviderTest {

    @InjectMocks
    private AsyncHashProvider asyncHashProvider;

    @Mock
    private HashGenerator hashGenerator;

    @Test
    public void getHashes_SuccessfullyReturnsHashes() {
        int anyAmount = 10;
        asyncHashProvider.getHashes(anyAmount);

        verify(hashGenerator).getHashes(anyAmount);
    }



}
