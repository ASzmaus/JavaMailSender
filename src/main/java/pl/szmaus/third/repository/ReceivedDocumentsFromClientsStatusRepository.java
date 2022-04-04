package pl.szmaus.third.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.third.entity.ReceivedDocumentsFromClientsStatus;

@Repository
public interface ReceivedDocumentsFromClientsStatusRepository extends CrudRepository<ReceivedDocumentsFromClientsStatus,Integer> {
}
