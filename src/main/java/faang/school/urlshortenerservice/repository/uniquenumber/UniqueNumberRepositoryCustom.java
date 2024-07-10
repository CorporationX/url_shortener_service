package faang.school.urlshortenerservice.repository.uniquenumber;

import org.springframework.stereotype.Repository;

@Repository
public interface UniqueNumberRepositoryCustom {
    long getLastUniqueNumber();
    void setLastUniqueNumber(long finalNumber);
}
