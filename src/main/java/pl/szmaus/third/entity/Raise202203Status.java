package pl.szmaus.third.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "raise202203Status")
public class Raise202203Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;
    @Column(name =  "Content")
    private String content;

    @Override
    public String toString() {
        return "Content tabel [" + "id=" + id + ", content=" + content +"]";
    }
}
