package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class MockHashCache implements HashCache {
    @Override
    public String getHash() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}