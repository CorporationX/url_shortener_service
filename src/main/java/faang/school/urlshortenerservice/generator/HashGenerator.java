package faang.school.urlshortenerservice.generator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@Slf4j
public class HashGenerator {

    @Value("${application.constants.number-of-unique.hashes}")
    private long numberOfUniqueHashes;

    public List<String> generateBatch(List<String> hashes) {

        return null;
    }
}
