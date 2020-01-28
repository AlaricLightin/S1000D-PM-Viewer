package ru.biderman.s1000dpmviewer.rest;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import ru.biderman.s1000dpmviewer.domain.Publication;
import ru.biderman.s1000dpmviewer.domain.PublicationDetails;
import ru.biderman.s1000dpmviewer.exceptions.PublicationNotFoundException;
import ru.biderman.s1000dpmviewer.services.PublicationDetailsService;
import ru.biderman.s1000dpmviewer.services.PublicationService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("Контроллер по работе с публикациями ")
@WebMvcTest(PublicationController.class)
class PublicationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    PublicationDetailsService detailsService;

    @MockBean
    PublicationService publicationService;

    private static final String PUB_CODE = "CODE";
    private static final long PUB_ID = 101;

    private PublicationDetails createTestDetails(long id, String code) {
        PublicationDetails result = new PublicationDetails();
        result.setId(id);
        result.setCode(code);
        result.setLanguage("");
        result.setIssue("");
        return result;
    }

    @DisplayName("должен возвращать детали всех публикаций")
    @Test
    void shouldGetAllDetail() throws Exception{
        PublicationDetails details1 = createTestDetails(PUB_ID, PUB_CODE);
        String code2 = "CODE2";
        PublicationDetails details2 = createTestDetails(PUB_ID + 1, code2);
        when(detailsService.findAll()).thenReturn(Arrays.asList(details1, details2));

        mockMvc.perform(get("/publication").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(PUB_ID))
                .andExpect(jsonPath("$[0].code").value(PUB_CODE))
                .andExpect(jsonPath("$[1].code").value(code2))
                .andReturn();
    }

    @DisplayName("должен загружать файл с новой публикацией")
    @Test
    void shouldLoadPublicationFile() throws Exception {
        final String content = "Test stream";
        InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile = new MockMultipartFile("file", contentStream);

        PublicationDetails details = createTestDetails(PUB_ID, PUB_CODE);
        Publication result = mock(Publication.class);
        when(result.getId()).thenReturn(PUB_ID);
        when(result.getDetails()).thenReturn(details);
        when(publicationService.add(any())).thenReturn(result);

        mockMvc.perform(multipart("/publication").file(multipartFile))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        UriComponentsBuilder.newInstance()
                                .scheme("http")
                                .host("localhost")
                                .path("/publication/{id}")
                                .buildAndExpand(PUB_ID)
                                .toUriString()
                ))
                .andExpect(jsonPath("$.id").value(PUB_ID))
                .andExpect(jsonPath("$.code").value(PUB_CODE))
                .andReturn();

        ArgumentCaptor<InputStream> argumentCaptor = ArgumentCaptor.forClass(InputStream.class);
        verify(publicationService).add(argumentCaptor.capture());
        assertThat(IOUtils.toString(argumentCaptor.getValue(), StandardCharsets.UTF_8)).isEqualTo(content);
    }

    @DisplayName("должен удалять публикацию")
    @Test
    void shouldDelete() throws Exception {
        mockMvc.perform(delete("/publication/{id}", PUB_ID))
                .andExpect(status().isOk())
                .andReturn();

        verify(publicationService).deleteById(PUB_ID);
    }

    @DisplayName("должен бросать исключение, если удаляемой публикации нет")
    @Test
    void shouldThrowExceptionIfDeletingAbsent() throws Exception{
        doThrow(PublicationNotFoundException.class).when(publicationService).deleteById(PUB_ID);
        mockMvc.perform(delete("/publication/{id}", PUB_ID))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}