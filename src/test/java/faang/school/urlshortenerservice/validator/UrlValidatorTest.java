package faang.school.urlshortenerservice.validator;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import faang.school.urlshortenerservice.validator.UrlValidator;

import static org.junit.jupiter.api.Assertions.*;


class UrlValidatorTest {

    @Ignore
    @Test
    public void testValidateUrlNotThrowException() {
        //Todo проверить, что валидатор не откидывает исключение на валидном url
//        fail();
    }

    @Ignore
    @Test
    public void testValidateUrlThrowException() {
        //ToDo проверить, что валидатор откдыдываеи исключение на невалидном url
        // и при пустом значении переданного параметра
//        fail();
    }

}