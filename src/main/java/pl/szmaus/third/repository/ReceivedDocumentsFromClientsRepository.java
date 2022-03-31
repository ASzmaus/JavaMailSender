package pl.szmaus.third.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.szmaus.third.entity.ReceivedDocumentsFromClients;
import pl.szmaus.third.entity.ReceivedDocumentsFromClientsStatus;

import java.util.List;

@Repository
public interface ReceivedDocumentsFromClientsRepository extends CrudRepository<ReceivedDocumentsFromClients,Integer> {
    ReceivedDocumentsFromClients findByIdFirm(Integer id);
    ReceivedDocumentsFromClientsStatus findByIdReceivedDocumentsFromClientsStatus(Integer idFirm);
    List<ReceivedDocumentsFromClients> findAllByIdFirm(Integer idFirms);
}
