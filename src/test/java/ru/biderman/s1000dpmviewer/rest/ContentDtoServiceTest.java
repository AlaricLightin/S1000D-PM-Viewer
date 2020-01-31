package ru.biderman.s1000dpmviewer.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.DMRef;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Entry;
import ru.biderman.s1000dpmviewer.domain.publicationcontent.Ident;
import ru.biderman.s1000dpmviewer.rest.dto.ContentItem;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DisplayName("Сервис для создания dto для элементов публикации ")
class ContentDtoServiceTest {
    private ContentDtoService contentDtoService;

    @BeforeEach
    void init() {
        contentDtoService = new ContentDtoService();
    }

    @DisplayName("должен создавать dto")
    @Test
    void shouldCreateDto() {
        DMRef dmRef = new DMRef(new Ident("CODE", "001", "RU-RU"), "DM Title");
        Entry childEntry = new Entry("Child Entry", Collections.emptyList());
        Entry parentEntry = new Entry("Parent Entry", Arrays.asList(dmRef, childEntry));

        ContentItem resultChild1 = new ContentItem(ContentDtoService.getDmNameString(dmRef), Collections.emptyList());
        ContentItem resultChild2 = new ContentItem(childEntry.getTitle(), Collections.emptyList());
        ContentItem result = new ContentItem(parentEntry.getTitle(), Arrays.asList(resultChild1, resultChild2));

        assertThat(contentDtoService.createContentItems(parentEntry))
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(result);
    }
}