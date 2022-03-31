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
@Table(name = "receivedDocumentsFromClients")
public class ReceivedDocumentsFromClients {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;
    @Column(name = "IdFirm")
    private Integer idFirm;
    @Column(name = "Number")
    private Integer number;
    @Column(name = "IdReceivedDocumentsFromClientsStatus")
    private Integer idReceivedDocumentsFromClientsStatus;
    @Column(name = "Date")
    private String data;

    @Override
    public String toString() {
        return "Contact tabela ["  + "number=" + number +   "idFirmy=" + idFirm + ", idReceivedDocumentsFromClientsStatus=" + idReceivedDocumentsFromClientsStatus +"]";
    }

}
