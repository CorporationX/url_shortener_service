package faang.school.urlshortenerservice.service;

import java.util.List;

public interface HashService {

    void saveHashes ();

    List<String> getHashes(long amount);
}
