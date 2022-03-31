package pl.szmaus.secondary.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "GM_FS")

    public class GmFs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "NUMER")
    private String number;
    @Column(name="ROK")
    private String year;
    @Column(name="MIESIAC")
    private String month;
    @Column(name = "DATA_WYSTAWIENIA")
    private LocalDate issueInvoiceDate;
    @Column(name="PLN_WARTOSC_BRUTTO")
    private BigDecimal grossAmountInPln;
    @Column(name="NAZWA_PELNA_ODBIORCY")
    private String fullNameReceiver;
    @Column(name="ID_ODBIORCY")
    private Integer idReceiver;
    @Column(name="NIP_ODBIORCY")
    private String taxIdReceiver;
    @Column(name="STATUS")
    private String status;
    @Column(name="UTWORZONO_PDF")
    private String createPdf;
    @Column(name="GUID")
    private String guid;
    @Column(name="NAZWA_SPOSOBU_PLATNOSCI")
    private String nameOfPayment;

        @Override
        public String toString() {
            return "Sales invoices [" + "Numer Invoice=" + number + ", Year=" + year + ", Month=" + month + ", issuing of invoice date " + issueInvoiceDate + ", Receiver: " + fullNameReceiver + ", TaxId Receiver= " + taxIdReceiver +", gross amount= " + grossAmountInPln+", status " +getStatus()+ ", name of payment" + nameOfPayment+"]";
        }
}