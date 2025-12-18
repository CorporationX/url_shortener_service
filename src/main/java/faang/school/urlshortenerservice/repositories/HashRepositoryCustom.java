package faang.school.urlshortenerservice.repositories;

import java.util.List;

public interface HashRepositoryCustom {
    List<String> findAndDelete(long amount);
}
