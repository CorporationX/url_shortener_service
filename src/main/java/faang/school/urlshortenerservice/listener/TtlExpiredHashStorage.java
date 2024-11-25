package faang.school.urlshortenerservice.listener;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TtlExpiredHashStorage {

    private final Set<String> hashesSet = ConcurrentHashMap.newKeySet();

    public void addHash(String hash) {
        hashesSet.add(hash);
    }

    public List<String> getHashesToUpdate() {
        List<String> hashes = new ArrayList<>(hashesSet);
        hashesSet.clear();
        return hashes;
    }
}