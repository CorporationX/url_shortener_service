package faang.school.urlshortenerservice.encoder;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    private static final char[] DICTIONARY =
            {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I',
            'J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i',
            'j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private static final int baseValue = DICTIONARY.length;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream().map(number -> {
            StringBuilder hash = new StringBuilder();
            while (number > 0) {
                hash.insert(0, DICTIONARY[(int) (number % baseValue)]) ;
                number /= baseValue;
            }
            return hash.toString();
        }).toList();
    }
}
