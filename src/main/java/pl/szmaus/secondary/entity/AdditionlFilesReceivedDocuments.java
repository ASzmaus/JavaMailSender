package pl.szmaus.secondary.entity;

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
@Table(name = "FI_KART_INNE_POZ")
public class AdditionlFilesReceivedDocuments {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;
	@Column(name = "ID_INNE_KART")
	private Integer idAdditionlFilesReceivedDocumentsStatus;
	@Column(name = "NUMER")
	private String number;
	@Column(name = "NAZWA")
	private String name;
	@Column(name = "WALUTA")
	private String currency;
	@Column(name = "OPIS")
	private String	description;

	@Override
	public String toString() {
		return "AdditionlFilesReceivedDocuments [id" + id+ "idAdditionlFilesReceivedDocumentsStatus"+ idAdditionlFilesReceivedDocumentsStatus+ "number=" + number +", name=" + name + "currency" + currency +"description"+description+"]";
	}


}
