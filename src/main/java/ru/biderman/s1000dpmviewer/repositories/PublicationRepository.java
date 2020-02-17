package ru.biderman.s1000dpmviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import ru.biderman.s1000dpmviewer.domain.Publication;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    @PostFilter("hasPermission(filterObject, 'READ')")
    List<Publication> findAll();

    @PostAuthorize("hasPermission(returnObject.orElse(null), 'READ')")
    Optional<Publication> findById(Long id);
}
