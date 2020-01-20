package ru.biderman.s1000dpmviewer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.biderman.s1000dpmviewer.domain.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
