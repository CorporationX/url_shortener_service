package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Util {

    public <T> List<List<T>> getBatches(List<T> listToDivide, int batchesQuantity) {
        List<List<T>> batches = new ArrayList<>();
        int batchSize = listToDivide.size() / batchesQuantity;
        int remainder = listToDivide.size() % batchesQuantity;

        int start = 0;
        for (int i = 0; i < batchesQuantity; i++) {
            int end = start + batchSize + (i < remainder ? 1 : 0);
            batches.add(new ArrayList<>(listToDivide.subList(start, end)));
            start = end;
        }
        return batches;
    }
}
