package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseEncoder {
    @Value("${encoder.base.alphabet}")
    private String alphabet;

    public List<String> encode(List<Long> uniqueNumbers) {
        int alphabetLength = alphabet.length();
        List<String> hashes = new ArrayList<>();
        for(Long uniqueNumber : uniqueNumbers){
            StringBuilder hash = new StringBuilder();
            while (uniqueNumber != 0) {
                hash.append(alphabet.charAt((int) (uniqueNumber % alphabetLength)));
                uniqueNumber /= alphabetLength;
            }
            hashes.add(hash.reverse().toString());
        }
        return hashes;
    }
}
