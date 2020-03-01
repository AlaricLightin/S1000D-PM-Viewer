package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.repositories.PublicationDetailsRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DisplayName("Сервис информации о публикации ")
class PublicationDetailsServiceImplTest {
    private PublicationDetailsServiceImpl service;
    private PublicationDetailsRepository repository;

    @BeforeEach
    void init() {
        repository = mock(PublicationDetailsRepository.class);
        service = new PublicationDetailsServiceImpl(repository);
    }

    @DisplayName("должен возвращать детали всех публикаций")
    @Test
    void shouldFindAll() {
        PublicationDetails details1 = mock(PublicationDetails.class);
        PublicationDetails details2 = mock(PublicationDetails.class);
        List<PublicationDetails> detailsList = Arrays.asList(details1, details2);
        when(repository.findAll()).thenReturn(detailsList);
        assertThat(service.findAll())
                .isEqualTo(detailsList);
    }

    @DisplayName("должен возвращать детали по id")
    @Test
    void shouldFindById() {
        PublicationDetails details = mock(PublicationDetails.class);
        long publicationId = 101;
        when(repository.findById(publicationId)).thenReturn(Optional.of(details));
        assertThat(service.findById(publicationId))
                .containsSame(details);
    }
}