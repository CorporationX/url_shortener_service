package faang.school.urlshortenerservice.service;

import java.util.List;

public interface HashGeneratorService {
    List<String> generateHashes(int count);

    void saveGeneratedHashesToDatabase(List<String> hashes);
}