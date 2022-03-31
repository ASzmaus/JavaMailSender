package pl.szmaus.third.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.third.entity.EKsiegowy;
import pl.szmaus.third.repository.EKsiegowyUserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EKsiegowyService {
    @Autowired
    EKsiegowyUserRepository eKsiegowyUserRepository;

    @Transactional
    public List<EKsiegowy> eKsiegowyList() {
        Iterable<EKsiegowy> iterator =eKsiegowyUserRepository.findAll();

        return StreamSupport
                .stream(iterator.spliterator(), true)
                .collect(Collectors.toList());
    }
}