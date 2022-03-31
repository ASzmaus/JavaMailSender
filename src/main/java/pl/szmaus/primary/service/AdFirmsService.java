package pl.szmaus.primary.service;

import org.springframework.stereotype.Service;
import pl.szmaus.primary.entity.AdFirms;
import pl.szmaus.primary.repository.AdFirmsRepository;
import java.util.List;

@Service
public class AdFirmsService {

    private final AdFirmsRepository adFirmsRepository;

    public AdFirmsService(AdFirmsRepository adFirmsRepository) {
        this.adFirmsRepository = adFirmsRepository;
    }

    public void verificationIfTaxIdIsValid() {
        Iterable<AdFirms> allFirms = adFirmsRepository.findAll();
        allFirms.forEach(d -> {
            if (d.getTaxId() != null) {
                d.getTaxId().replaceAll("\\D", "");
                d.setTaxId(d.getTaxId().replaceAll("\\D", ""));
            }
        });
    }

    public Boolean ifSizeOfAdFirmsListIsMoreThenOne( List<AdFirms> firmsList) {
        if (firmsList.size()>1)
            return true;
        else
            return false;
    }

    public Boolean ifEmailAdressExists( AdFirms adFirms) {
        if (adFirms.getFirmEmailAddress() != null)
            return true;
        else
            return false;
    }
}
