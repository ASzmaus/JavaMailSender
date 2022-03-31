package pl.szmaus.third.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szmaus.third.entity.EKsiegowy;
import pl.szmaus.third.entity.Raise202203;
import pl.szmaus.third.repository.EKsiegowyUserRepository;
import pl.szmaus.third.repository.Raise202203Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class Raise202203Service {
    @Autowired
    Raise202203Repository raise202203Repository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public List<Raise202203> raise202203List() {
        Iterable<Raise202203> iteratorRaise = raise202203Repository.findAll();
        return StreamSupport
                .stream(iteratorRaise.spliterator(), true)
                .collect(Collectors.toList());
    }
}