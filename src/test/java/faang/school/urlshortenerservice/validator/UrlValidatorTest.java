package faang.school.urlshortenerservice.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {
    @InjectMocks
    private UrlValidator urlValidator;
    @Test
    public void validateNullTest(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.validate(null));
    }

    @Test
    public void validateNotUrlTest(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.validate("https://google com/"));
    }

    @Test
    public void validateTest(){
        urlValidator.validate("https://google.com/");
    }
}
