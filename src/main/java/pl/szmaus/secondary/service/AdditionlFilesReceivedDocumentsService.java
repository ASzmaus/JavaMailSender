package pl.szmaus.secondary.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.secondary.entity.AdditionlFilesReceivedDocuments;
import pl.szmaus.exception.EntityNotFoundException;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.secondary.repository.AdditionlFilesReceivedDocumentsRepository;
import static java.time.LocalDate.now;

@Service
public class AdditionlFilesReceivedDocumentsService {

    private final AdFirmsRepository adFirmsRepository;
    private final AdditionlFilesReceivedDocumentsRepository additionlFilesReceivedDocumentsRepository;

    public AdditionlFilesReceivedDocumentsService(AdFirmsRepository adFirmsRepository, AdditionlFilesReceivedDocumentsRepository additionlFilesReceivedDocumentsRepository) {
        this.adFirmsRepository = adFirmsRepository;
        this.additionlFilesReceivedDocumentsRepository = additionlFilesReceivedDocumentsRepository;
    }

    @Transactional
    public Boolean checkIfRecivedDocumentFromFirebird(Integer idFirm) {
        AdFirms adFirms = adFirmsRepository.findById(idFirm).orElseThrow(EntityNotFoundException::new);
        if (idFirm == null || adFirms.getNumber()== null)
            throw new IllegalArgumentException("IdFirm or NumberRaks cannot be null");
        String  numberRaks = adFirms.getNumber().toString();
        AdditionlFilesReceivedDocuments additionlFilesReceivedDocuments= additionlFilesReceivedDocumentsRepository.findByNumber(numberRaks);
        if(additionlFilesReceivedDocuments!=null && additionlFilesReceivedDocuments.getNumber().equals(adFirms.getNumber().toString()) && additionlFilesReceivedDocuments.getName().substring(0,7).equals(now().minusMonths(1).toString().substring(0,7)) ){
            return true;
        }else {
            return false;
        }
    }
}