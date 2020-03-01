package ru.biderman.s1000dpmviewer.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.repositories.PublicationDetailsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicationDetailsServiceImpl implements PublicationDetailsService {
    private final PublicationDetailsRepository repository;

    @Override
    public List<PublicationDetails> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PublicationDetails> findById(long id) {
        return repository.findById(id);
    }
}
