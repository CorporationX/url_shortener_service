package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class HashGenerator {

    private HashRepository hashRepository;
    private Base62Encoder base62Encoder;

    @Value("${hash.range:1000}")
    private int maxRange;

    @Transactional
    public void generateHashe() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        range.forEach(()-> );


    }





}
