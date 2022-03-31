package pl.szmaus.third.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.third.entity.EKsiegowy;

@Repository
public interface EKsiegowyUserRepository extends CrudRepository<EKsiegowy,Integer> {
}
