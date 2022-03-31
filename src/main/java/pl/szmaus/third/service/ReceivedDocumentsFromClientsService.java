package pl.szmaus.third.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.primary.repository.AdFirmsRepository;
import pl.szmaus.third.entity.ReceivedDocumentsFromClients;
import pl.szmaus.third.repository.ReceivedDocumentsFromClientsRepository;

import static java.time.LocalDate.now;

@Service
public class ReceivedDocumentsFromClientsService {

    private final ReceivedDocumentsFromClientsRepository receivedDocumentsFromClientsRepository;
    private final AdFirmsRepository adFirmsRepository;

    public ReceivedDocumentsFromClientsService(ReceivedDocumentsFromClientsRepository receivedDocumentsFromClientsRepository, AdFirmsRepository adFirmsRepository) {
        this.receivedDocumentsFromClientsRepository = receivedDocumentsFromClientsRepository;
        this.adFirmsRepository = adFirmsRepository;
    }
    @Transactional
    public void saveReceivedDocumentsFromClients(ReceivedDocumentsFromClients receivedDocumentsFromClients, Integer idFirm, Integer idReceivedDocumentsFromClientsStatus) {
        AdFirms adfirms = adFirmsRepository.findById(idFirm)
                .orElseThrow(() -> new RuntimeException("No firms for this Id"));
        receivedDocumentsFromClients.setIdFirm(idFirm);
        receivedDocumentsFromClients.setData(now().minusMonths(1).toString().substring(0, 7));
        receivedDocumentsFromClients.setIdReceivedDocumentsFromClientsStatus(idReceivedDocumentsFromClientsStatus);
        receivedDocumentsFromClients.setNumber(adfirms.getNumber());
        receivedDocumentsFromClientsRepository.save(receivedDocumentsFromClients);
    }

    @Transactional
    public void editReceivedDocumentsFromClients(ReceivedDocumentsFromClients receivedDocumentsFromClients, Integer idReceivedDocumentsFromClientsStatus) {
        receivedDocumentsFromClients.setIdReceivedDocumentsFromClientsStatus(idReceivedDocumentsFromClientsStatus);
        receivedDocumentsFromClients.setData(now().minusMonths(1).toString().substring(0, 7));
        receivedDocumentsFromClientsRepository.save(receivedDocumentsFromClients);
    }

}
