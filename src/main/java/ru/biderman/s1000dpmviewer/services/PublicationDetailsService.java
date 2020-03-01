package ru.biderman.s1000dpmviewer.services;

import ru.biderman.s1000dpmviewer.domain.PublicationDetails;

import java.util.List;
import java.util.Optional;

public interface PublicationDetailsService {
    List<PublicationDetails> findAll();
    Optional<PublicationDetails> findById(long id);
}
