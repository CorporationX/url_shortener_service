package faang.school.urlshortenerservice.hash;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HashGenerator {

    @Value("${alphabet}")
    private String alphabet;


}
