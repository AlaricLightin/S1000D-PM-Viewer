package ru.biderman.s1000dpmviewer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.exceptions.InvalidPublicationException;
import ru.biderman.s1000dpmviewer.exceptions.PublicationAlreadyExistsException;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.repositories.PublicationDetailsRepository;
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
    private PublicationDetailsRepository detailsRepository;

    private final static long PUBLICATION_ID = 101;

    private final static String MOCK_PUBLICATION_CODE = "CODE";
    private final static String MOCK_PUBLICATION_LANGUAGE = "LANGUAGE";
    private final static String MOCK_PUBLICATION_ISSUE = "ISSUE";

    private Publication createMockPublication() {
        Publication publication = mock(Publication.class);

        PublicationDetails details = mock(PublicationDetails.class);
        when(details.getCode()).thenReturn(MOCK_PUBLICATION_CODE);
        when(details.getLanguage()).thenReturn(MOCK_PUBLICATION_LANGUAGE);
        when(details.getIssue()).thenReturn(MOCK_PUBLICATION_ISSUE);
        when(publication.getDetails()).thenReturn(details);
        return publication;
    }

    @BeforeEach
    void init() {
        publicationRepository = mock(PublicationRepository.class);
        publicationParser = mock(PublicationParser.class);
        detailsRepository = mock(PublicationDetailsRepository.class);
        publicationService = new PublicationServiceImpl(publicationRepository, detailsRepository, publicationParser);
    }

    @DisplayName("должен добавлять публикацию")
    @Test
    void shouldAddPublication() throws Exception{
        InputStream inputStream = mock(InputStream.class);
        Publication publication = createMockPublication();
        Publication expectedSavedPublication = mock(Publication.class);
        when(publicationParser.createPublication(inputStream)).thenReturn(publication);
        when(detailsRepository.existsByCodeAndIssueAndLanguage(
                MOCK_PUBLICATION_CODE, MOCK_PUBLICATION_ISSUE, MOCK_PUBLICATION_LANGUAGE)).thenReturn(false);
        when(publicationRepository.save(publication)).thenReturn(expectedSavedPublication);

        Publication savedPublication = publicationService.add(inputStream);
        assertThat(savedPublication).isEqualTo(expectedSavedPublication);
    }

    @DisplayName("должен бросать исключение, если не удалось распарсить публикацию")
    @Test
    void shouldThrowExceptionIfCouldNotParse() throws InvalidPublicationException {
        InputStream inputStream = mock(InputStream.class);
        doThrow(InvalidPublicationException.class).when(publicationParser).createPublication(inputStream);
        assertThrows(InvalidPublicationException.class, () -> publicationService.add(inputStream));
    }

    @DisplayName("должен бросать исключение, если публикация с такими идентификационными данными уже есть")
    @Test
    void shouldThrowExceptionIfPublicationExists() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        Publication publication = createMockPublication();
        when(publicationParser.createPublication(inputStream)).thenReturn(publication);
        when(detailsRepository.existsByCodeAndIssueAndLanguage(
                MOCK_PUBLICATION_CODE, MOCK_PUBLICATION_ISSUE, MOCK_PUBLICATION_LANGUAGE))
                .thenReturn(true);
        assertThrows(PublicationAlreadyExistsException.class, () -> publicationService.add(inputStream));
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