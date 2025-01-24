package faang.school.urlshortenerservice.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class Base62ValidatorTest {
    private Base62Validator base62Validator;

    @BeforeEach
    void setUp() {
        base62Validator = new Base62Validator();
    }
    @Test
    void testCheckListThrownException() {
        assertThrows(IllegalArgumentException.class,
                () -> base62Validator.checkList(null));
    }
    @Test
    void testCheckListCorrectWork() {
        assertDoesNotThrow(() -> base62Validator.checkList(new ArrayList<>()));
    }
}