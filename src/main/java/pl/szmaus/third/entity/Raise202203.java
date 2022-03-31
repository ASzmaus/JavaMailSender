package pl.szmaus.third.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "raise202203")
public class Raise202203 {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "number")
	private Integer	number;
	@Column(name = "baseAmount")
	private Double baseAmount;
	@Column(name = "raiseInPln")
	private Double raiseInPln;
	@Column(name = "newPrice")
	private Double newPrice;
	@Column(name = "raiseInPercentage")
	private Double raiseInPercentage;
	@Column(name = "idRaise202203Status")
	private Integer	idRaise202203Status;
}
