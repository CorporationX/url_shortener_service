package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE_62_CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final HashRepository hashRepository;

    @Value("${hash.range.max:10000}")
    private int maxRange;
    @Transactional
    public void generateHash(){
        List<Long> range  = hashRepository.getUniqueNumbers(maxRange);
        List<String> hashes = range.stream()
                .map(this::applyBase62Encoding)
                .toList();
        hashRepository.save(hashes);
    }

    @Transactional
    public  List<String> getHashes(long amount){
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if(hashes.size() < amount){
            generateHash();
            hashes.addAll(hashRepository.getHashBatch(amount-hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Transactional
    @Async
    public CompletableFuture<List<String>> getHashesAsync(long amount){
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String applyBase62Encoding(long number) {
        if (number == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE_62_CHARACTERS.length());
            sb.append(BASE_62_CHARACTERS.charAt(remainder));
            number = number / BASE_62_CHARACTERS.length();
        }
        return sb.reverse().toString();
    }
}
