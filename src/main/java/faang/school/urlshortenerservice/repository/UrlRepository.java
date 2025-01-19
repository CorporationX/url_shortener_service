package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends JpaRepository<Url, String> {

  @Query(nativeQuery = true, value = """
      INSERT INTO url (url, hash, created_at)
      VALUES (?1, ?2, NOW()) RETURNING *
      """)
  Url create(String url, String hash);

  @Query(nativeQuery = true, value = """
      DELETE FROM url WHERE age(NOW(), created_at) > '1 year' ? RETURNING *
      """)
  List<String> delete();

}
