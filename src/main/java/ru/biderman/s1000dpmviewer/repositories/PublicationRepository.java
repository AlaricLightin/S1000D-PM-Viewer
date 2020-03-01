package ru.biderman.s1000dpmviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import ru.biderman.s1000dpmviewer.domain.Publication;

import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    @PostAuthorize("!returnObject.present || hasPermission(returnObject.get(), 'READ')")
    Optional<Publication> findById(Long id);
}
