package pl.szmaus.third.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.third.entity.EKsiegowyStatus;

@Repository
public interface Raise202203StatusRepository extends CrudRepository<EKsiegowyStatus,Integer> {
}
