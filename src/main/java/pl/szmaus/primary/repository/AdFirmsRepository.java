package pl.szmaus.primary.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.primary.entity.AdFirms;

import java.util.List;

@Repository
public interface AdFirmsRepository extends CrudRepository<AdFirms,Integer>{
	AdFirms findByTaxId(String name);
	AdFirms findByNumber(Integer number);
	List<AdFirms> findAllByTaxId(String name);
}
