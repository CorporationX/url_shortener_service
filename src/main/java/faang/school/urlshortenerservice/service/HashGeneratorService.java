package faang.school.urlshortenerservice.service;

import org.springframework.beans.factory.annotation.Value;

public abstract class HashGeneratorService {

    @Value("${server.hash.generate.batch-size}")
    protected int batchSizeForGenerateFreeHashes;

    public abstract void generateFreeHashes();
}
