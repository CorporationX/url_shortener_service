package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import io.netty.handler.codec.base64.Base64Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorServiceImpl implements HashGeneratorService {

    HashRepository hashRepository;
    Base64Encoder base64Encoder;

    @Value("${spring.jpa.amount-hash}")
    private int amountHash;

    @Override
    public List<Hash> generateBatch() {
        List<Hash> hashes = hashRepository.findTopNHashes(amountHash);

        return List.of();
    }
}
