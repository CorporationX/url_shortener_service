package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Builder
@Table(name = "hash")
@NoArgsConstructor
@AllArgsConstructor
public class Hash {
    @Id
    @Size(min = 6, max = 6)
    @NotBlank(message = "Hash cannot be blank")
    private String hash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Hash hash1 = (Hash) o;
        return getHash() != null && Objects.equals(getHash(), hash1.getHash());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
