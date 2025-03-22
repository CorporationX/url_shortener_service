package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashRepositoryCustom {

    void saveAll(List<String> hashes);
}