package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Slf4j
public class Base62Validator {
    public void checkList(List<Long> numbers){
        if(numbers==null){
            log.warn("list of numbers must not be null");
            throw new IllegalArgumentException("invalid argument, list of numbers must not be null");
        }
    }
}
