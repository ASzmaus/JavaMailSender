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
@Table(name = "eKsiegowy")
public class EKsiegowy {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Id")
	private Integer id;
	@Column(name = "RaksNo")
	private Integer raksNo;
	@Column(name = "Name")
	private String name;
	@Column(name = "Surname")
	private String surname;
	@Column(name = "CompanyName")
	private String companyName;
	@Column(name = "Email")
	private String	email;
	@Column(name = "Password")
	private String	password;
	@Column(name = "IdEKsiegowyStatus")
	private Integer	idEKsiegowyStatus;



	@Override
	public String toString() {
		return "eKsiegowy [id=" + id + ", raksno=" + raksNo + ", name=" + name + ", surnamename=" + surname + ", email=" + email  + ", status=" + idEKsiegowyStatus +"]";
	}
}
