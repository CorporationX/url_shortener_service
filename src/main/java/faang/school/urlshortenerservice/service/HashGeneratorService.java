package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashGeneratorService {

    List<Hash> generateBatch();
}
