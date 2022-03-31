package pl.szmaus.secondary.repository;


import org.springframework.data.repository.CrudRepository;
import pl.szmaus.secondary.entity.GmFs;

public interface GmFsRepository extends CrudRepository<GmFs,Integer> {

        GmFs findAllByNumber(String s);
        GmFs findByGuid(String s);

}
