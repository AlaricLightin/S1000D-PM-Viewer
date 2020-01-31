package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.repositories.PublicationRepository;
import ru.biderman.s1000dpmviewer.xmlparsers.PublicationParser;

import java.io.InputStream;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DisplayName("Сервис по работе с публикациями ")
class PublicationServiceImplTest {
    private PublicationServiceImpl publicationService;
    private PublicationRepository publicationRepository;
    private PublicationParser publicationParser;

    private final static long PUBLICATION_ID = 101;

    @BeforeEach
    void init() {
        publicationRepository = mock(PublicationRepository.class);
        publicationParser = mock(PublicationParser.class);
        publicationService = new PublicationServiceImpl(publicationRepository, publicationParser);
    }

    @DisplayName("должен добавлять публикацию")
    @Test
    void shouldAddPublication() {
        InputStream inputStream = mock(InputStream.class);
        Publication publication = mock(Publication.class);
        Publication expectedSavedPublication = mock(Publication.class);
        when(publicationParser.createPublication(inputStream)).thenReturn(publication);
        when(publicationRepository.save(publication)).thenReturn(expectedSavedPublication);

        Publication savedPublication = publicationService.add(inputStream);
        assertThat(savedPublication).isEqualTo(expectedSavedPublication);
    }

    @DisplayName("должен возвращать публикацию по id")
    @Test
    void shouldFindById() throws Exception{
        Publication publication = mock(Publication.class);
        when(publicationRepository.findById(PUBLICATION_ID)).thenReturn(Optional.of(publication));
        assertThat(publicationService.findById(PUBLICATION_ID))
                .isEqualTo(publication);
    }

    @DisplayName("должен бросать исключение, если публикации нет")
    @Test
    void shouldThrowExceptionIfFindAbsent() {
        when(publicationRepository.findById(PUBLICATION_ID)).thenReturn(Optional.empty());
        assertThrows(PublicationNotFoundException.class, () -> publicationService.findById(PUBLICATION_ID));
    }

    @DisplayName("должен удалять публикацию")
    @Test
    void shouldDeleteById() throws Exception{
        when(publicationRepository.existsById(PUBLICATION_ID)).thenReturn(true);
        publicationService.deleteById(PUBLICATION_ID);
        verify(publicationRepository).deleteById(PUBLICATION_ID);
    }

    @DisplayName("должен бросать исключение, если удаляемой публикации нет")
    @Test
    void shouldThrowExceptionIfDeletingAbsent() {
        when(publicationRepository.existsById(PUBLICATION_ID)).thenReturn(false);
        assertThrows(PublicationNotFoundException.class, () -> publicationService.deleteById(PUBLICATION_ID));
    }

    @DisplayName("должен возвращать содержимое публикации")
    @Test
    void shouldGetContent() throws PublicationNotFoundException{
        Publication publication = mock(Publication.class);
        Document document = mock(Document.class);
        Entry entry = mock(Entry.class);
        when(publication.getDocument()).thenReturn(document);
        when(publicationParser.getPublicationContent(document)).thenReturn(entry);
        when(publicationRepository.findById(PUBLICATION_ID)).thenReturn(Optional.of(publication));

        assertThat(publicationService.getContentById(PUBLICATION_ID)).isEqualTo(entry);
    }

    @DisplayName("должен бросать исключение, если требуется контент отсутствующей публикации")
    @Test
    void shouldThrowExceptionIfContentAbsent() {
        when(publicationRepository.findById(PUBLICATION_ID)).thenReturn(Optional.empty());
        assertThrows(PublicationNotFoundException.class, () -> publicationService.getContentById(PUBLICATION_ID));
    }
}