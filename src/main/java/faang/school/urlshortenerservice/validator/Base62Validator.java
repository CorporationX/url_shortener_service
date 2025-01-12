package faang.school.urlshortenerservice.validator;

import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class Base62Validator {
    public void checkList(List<Long> numbers){
        if(numbers==null){
            throw new IllegalArgumentException("invalid argument, list of numbers must not be null");
        }
    }
}
