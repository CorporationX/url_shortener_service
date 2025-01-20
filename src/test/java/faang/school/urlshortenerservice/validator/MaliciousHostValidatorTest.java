package faang.school.urlshortenerservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class MaliciousHostValidatorTest {

    private MaliciousHostValidator validator;

    @BeforeEach
    void setUp() throws Exception {
        validator = new MaliciousHostValidator();
        String blacklistContent = "badhost\nmalicious\n";
        ByteArrayResource resource = new ByteArrayResource(blacklistContent.getBytes());
        Field blacklistFileField = MaliciousHostValidator.class.getDeclaredField("blacklistFile");
        blacklistFileField.setAccessible(true);
        blacklistFileField.set(validator, resource);
        validator.init();
    }

    @Test
    void testSafeHost() {
        String safeUrl = "https://goodwebsite.com";
        boolean result = validator.isHostSafe(safeUrl);
        assertTrue(result);
    }

    @Test
    void testMaliciousHost() {
        String maliciousUrl = "https://badhost.example.com";
        boolean result = validator.isHostSafe(maliciousUrl);
        assertFalse(result);
    }

    @Test
    void testHostContainingForbiddenWord() {
        String maliciousUrl = "https://safe-malicious-site.com";
        boolean result = validator.isHostSafe(maliciousUrl);
        assertFalse(result);
    }

    @Test
    void testInvalidUrl() {
        String invalidUrl = "notaurl";
        boolean result = validator.isHostSafe(invalidUrl);
        assertFalse(result);
    }
}
