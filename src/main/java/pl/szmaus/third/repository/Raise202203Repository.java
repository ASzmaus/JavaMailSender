package pl.szmaus.third.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.third.entity.EKsiegowyStatus;
import pl.szmaus.third.entity.Raise202203;

@Repository
public interface Raise202203Repository extends CrudRepository<Raise202203,Integer> {
}
