package faang.school.urlshortenerservice.service.hash;

import java.util.List;

public interface HashService {

    List<String> getHashBatch(int quantity);

    void save(List<String> hashes);

    boolean isNeedGenerateHash();
}
