package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "hash")
@AllArgsConstructor
@NoArgsConstructor
public class Hash {
    @Id
    private Long id;

    private String hash;

    @Override
    public String toString(){
        return hash;
    }
}
