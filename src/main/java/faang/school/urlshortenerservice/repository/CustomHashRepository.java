package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface CustomHashRepository {

    List<Long> getNextRange(int maxRange);

    int[] saveAllBatch(List<Hash> hosts);
}
