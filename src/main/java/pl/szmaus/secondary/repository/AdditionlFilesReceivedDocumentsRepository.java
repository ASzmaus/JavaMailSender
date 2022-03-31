package pl.szmaus.secondary.repository;

import org.springframework.data.repository.CrudRepository;
import pl.szmaus.secondary.entity.AdditionlFilesReceivedDocuments;

public interface AdditionlFilesReceivedDocumentsRepository extends CrudRepository<AdditionlFilesReceivedDocuments,Integer> {


    AdditionlFilesReceivedDocuments findByNumber(String number);
}
