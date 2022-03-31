package pl.szmaus.secondary.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.secondary.entity.GmFs;
import pl.szmaus.secondary.repository.GmFsRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.time.LocalDate.now;

@Service
public class GmFsService {

    private static final String PAYMENT_METHOD = "Gotowka";
    private static final String BANK_ACCOUNT = "</strong>Numer konta bankowego: <strong> Bank XX XXXX XXXX XXXX XXXX XXXX XXXX </strong>";
    private static final String CASH= "</strong>Sposób płatności: <strong>Gotowka</strong>";

    private final GmFsRepository gmFsRepository;

    public GmFsService(GmFsRepository gmFsRepository) {
        this.gmFsRepository = gmFsRepository;
    }

    @Transactional
    public List<GmFs> currentMonthGmFsList() {
        Iterable<GmFs> iterator =gmFsRepository.findAll();
        return StreamSupport
                .stream(iterator.spliterator(), true)
                .filter(e->e.getIssueInvoiceDate().getMonth()==now().getMonth() && e.getIssueInvoiceDate().getYear()==now().getYear())
                .collect(Collectors.toList());
    }

    @Transactional
    public String paymentForInvoices(String s){
        if(s.equals(PAYMENT_METHOD)) {
            return CASH ;
        }else {
            return BANK_ACCOUNT;
        }
   }

}