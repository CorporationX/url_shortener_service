package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashRepository extends JpaRepository<Hash, String> {
    @Query(nativeQuery = true, value = """
            "SELECT nextval('unique_number_seq') AS generated_value" +
                        "FROM generated_series(1, 1000)"
            """)
    List<Long> getUniqueNumbers();

    @Query(nativeQuery = true, value = """
            "WITH selected_hashes AS (" +
                        "    SELECT hash FROM hash" +
                        "    ORDER BY RANDOM()" +
                        "    LIMIT :limit" +
                        ")" +
                        "DELETE FROM hash" +
                        "WHERE hash IN (SELECT hash FROM selected_hashes)" +
                        "RETURNING hash;"
            """)
    void getHashBatch(@Param("limit") int limit);
    @Query(nativeQuery = true, value = """
            "INSERT INTO hash (hash) VALUES :string_list"
            """)
    List<String> saveByHashList(@Param("string_list") List<String> stringList);
}
