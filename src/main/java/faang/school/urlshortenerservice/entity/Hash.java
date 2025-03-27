package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hash {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "hash")
    private String hash;
}
