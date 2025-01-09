package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.service.hash.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    /**
     * Сохранить список хэшей (batch).
     */
    @Transactional
    public void saveAllBatch(List<String> hashes) {
        hashRepository.save(hashes); // чисто JDBC вставка
    }

    /**
     * Получить (и сразу удалить) n случайных хэшей из таблицы.
     * Если вернулось меньше, чем запросили, генерируем ещё.
     */
    @Transactional
    public List<String> findAllByPackSize(int packSize) {
        List<String> found = hashRepository.getHashBatch(packSize);

        int shortCount = packSize - found.size();
        if (shortCount > 0) {
            // generateAndGet вернет новые хэши,
            // мы их тоже вставим
            found.addAll(hashGenerator.generateAndGet(shortCount));
        }
        return found;
    }

    @Transactional
    public List<Long> getUniqueNumbers(long size) {
        return hashRepository.getUniqueNumbers((int) size);
    }

    @Transactional(readOnly = true)
    public long getHashesSize() {
        return hashRepository.count();
    }

}