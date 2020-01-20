package ru.biderman.s1000dpmviewer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class XMLParseUtilsTest {
    @DisplayName("должен возвращать первый подэлемент")
    @Test
    void shouldGetFirstElement() {
        Document document = XMLDocumentUtils.getDocumentFromString("<a><b/><c/></a>");
        Element element = XMLParseUtils.getFirstChildElement(document.getDocumentElement(), "c");
        assertThat(element)
                .isNotNull()
                .satisfies(e -> assertThat(e.getTagName()).isEqualTo("c"));
    }

    @DisplayName("должен возвращать подэлемент по пути")
    @Test
    void shouldGetElementByPath() {
        Document document = XMLDocumentUtils.getDocumentFromString("<a><b><c/></b></a>");
        Element element = XMLParseUtils.getElement(document.getDocumentElement(), "//c");
        assertThat(element)
                .isNotNull()
                .satisfies(e -> assertThat(e.getTagName()).isEqualTo("c"));
    }

    @DisplayName("должен получать текст из нескольких атрибутов")
    @Test
    void shouldGetTextByAttrs() {
        Document document = XMLDocumentUtils.getDocumentFromString("<root><a attr1=\"a\" attr2=\"b\"/></root>");
        String result = XMLParseUtils.getDelimitedTextFromAttrs(
                document.getDocumentElement(), "a", "attr1", "attr2");
        assertThat(result).isEqualTo("a-b");
    }
}