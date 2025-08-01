package faang.school.urlshortenerservice.generator;

import java.util.List;

public interface HashGenerator {
    void generateHash();
    List<String> fetchHashes(int amount);
}
