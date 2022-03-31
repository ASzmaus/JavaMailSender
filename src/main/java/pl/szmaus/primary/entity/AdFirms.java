package pl.szmaus.primary.entity;

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
@Table(name = "AD_firms")
public class AdFirms {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "number")
	private Integer number;
	@Column(name = "shortname")
	private String fullname;
	@Column(name = "firm_email_address")
	private String	firmEmailAddress;
	@Column(name = "taxid")
	private String taxId;

	@Override
	public String toString() {
		return "AD_Firms [id=" + id +  "number=" + number +", shortname=" + fullname + ", firmEmailAddress=" + firmEmailAddress + "taxId"+ taxId+"]";
	}
}
