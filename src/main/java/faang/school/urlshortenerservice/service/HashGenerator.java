package faang.school.urlshortenerservice.service;

import java.util.List;

public interface HashGenerator {

  List<String> getHashes(long amount);
}
