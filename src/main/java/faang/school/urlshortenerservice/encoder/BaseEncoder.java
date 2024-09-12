package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BaseEncoder {
    @Value("${base62.alphabet}")
    private String alphabet;

    public List<String> getHashes(List<Long> uniqueNumbers) {
        List<String> hashes = new ArrayList<>();
        for(Long uniqueNumber : uniqueNumbers){
            StringBuilder hash = new StringBuilder();
            while (uniqueNumber != 0) {
                hash.append(alphabet.charAt((int) (uniqueNumber % 62)));
                uniqueNumber /= 62;
            }
            hashes.add(hash.toString());
        }
        return hashes;
    }
}
