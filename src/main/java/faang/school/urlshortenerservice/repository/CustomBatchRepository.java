package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface CustomBatchRepository<T> {

    <S extends T> List<S> saveAll(Iterable<S> entities);
}
