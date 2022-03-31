package pl.szmaus.third.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.third.entity.EKsiegowyStatus;

@Repository
public interface EKsiegowyStatusRepository extends CrudRepository<EKsiegowyStatus,Integer> {
}
