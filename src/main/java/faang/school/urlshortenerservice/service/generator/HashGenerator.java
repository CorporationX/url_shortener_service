package faang.school.urlshortenerservice.service.generator;

import java.util.List;

public interface HashGenerator {

    void generateBatch();

    List<String> getBatch();
}
