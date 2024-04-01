package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "hash")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hash {

    @Id
    @Column(name = "hash", unique = true, nullable = false, length = 6)
    private String hash;
}
