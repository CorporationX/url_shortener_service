package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.validator.Base62Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    @Value("${base62.chars}")
    private String base62Chars;
    @Value("${base62.length}")
    private int base62Length;
    private final Base62Validator validator;

    public List<String> encode(List<Long> numbers) {
        validator.checkList(numbers);

        List<String> resultList = new ArrayList<>();

        for (Long number : numbers) {
            StringBuilder result = new StringBuilder();
            while (number > 0) {
                int remainder = (int) (number % base62Length);
                result.append(base62Chars.charAt(remainder));
                number /= base62Length;
            }

            if (!result.isEmpty()) {
                resultList.add(result.reverse().toString());
            }
        }
        return resultList;
    }
}
