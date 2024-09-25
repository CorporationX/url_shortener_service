package faang.school.urlshortenerservice.util;

import java.util.List;
import java.util.function.Consumer;

public interface BatchProcessor<T> {

    void processBatches(List<T> source, Consumer<List<T>> task);
}
