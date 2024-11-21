package utils;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestData {
    public static List<String> generateHashes(int size, int length) {
        List<String> hashes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            hashes.add(UUID.randomUUID().toString().replace("-", "").substring(0, length));
        }
        return hashes;
    }
}
