package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.utils.UrlValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UrlValidatorTest {
    private final UrlValidator urlValidator = new UrlValidator();

    @Test
    public void isUrl_Success() {
        urlValidator.isUrl("https://githuuuuuuuuuuuuuuuub.rssssssssssssssss/sssssssssssss/ssssssssssssss/ssssss");
        urlValidator.isUrl("https://github.com");
        urlValidator.isUrl("http://github.com");
        urlValidator.isUrl("http://gi.ru");
        urlValidator.isUrl("https://r.r");
    }

    @Test
    public void isUrl_Error() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("htt://github.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("https:/github.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("https//github.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("http://.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("http://girhub."));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("htps://github.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("htp://github.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("ttp://github.com"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> urlValidator.isUrl("ttps://github.com"));
    }
}
