package ru.biderman.s1000dpmviewer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class XMLParseUtilsTest {
    @DisplayName("должен возвращать первый подэлемент")
    @Test
    void shouldGetFirstElement() {
        Document document = XMLDocumentUtils.getDocumentFromString("<a><b/><c/></a>");
        Optional<Element> element = XMLParseUtils.getFirstChildElement(document.getDocumentElement(), "c");
        assertThat(element)
                .isPresent()
                .map(Element::getTagName)
                .get()
                .isEqualTo("c");
    }

    @DisplayName("должен возвращать текст из первого подэлемента")
    @Test
    void shouldGetTextFromFirstChildElement() {
        String text = "Text";
        Document document = XMLDocumentUtils.getDocumentFromString("<a><b>" + text + "</b></a>");
        assertThat(XMLParseUtils.getTextFromChildElement(document.getDocumentElement(), "b"))
                .isEqualTo(text);
    }

    @DisplayName("должен возвращать подэлемент по пути")
    @Test
    void shouldGetElementByPath() {
        Document document = XMLDocumentUtils.getDocumentFromString("<a><b><c/></b></a>");
        Optional<Element> element = XMLParseUtils.getElement(document.getDocumentElement(), "//c");
        assertThat(element)
                .isPresent()
                .map(Element::getTagName)
                .get()
                .isEqualTo("c");
    }

    @DisplayName("должен возвращать текст из подэлемента по пути")
    @Test
    void shouldGetTextFromElementByPath() {
        String text = "Text";
        Document document = XMLDocumentUtils.getDocumentFromString("<a><b><c>" + text + "</c></b></a>");
        assertThat(XMLParseUtils.getTextFromElement(document, "//c"))
                .isEqualTo(text);
    }

    @DisplayName("должен получать текст из нескольких атрибутов")
    @Test
    void shouldGetTextByAttrs() {
        Document document = XMLDocumentUtils.getDocumentFromString("<root><a attr1=\"a\" attr2=\"b\"/></root>");
        Optional<String> result = XMLParseUtils.getDelimitedTextFromAttrs(
                document.getDocumentElement(), "a", "attr1", "attr2");
        assertThat(result)
                .isPresent()
                .get()
                .isEqualTo("a-b");
    }

    @DisplayName("должен получать дату из элемента")
    @Test
    void shouldGetDateFromElement() {
        Document document = XMLDocumentUtils.getDocumentFromString("<root><date year=\"2000\" month=\"04\" day=\"08\"/></root>");
        Optional<LocalDate> result = XMLParseUtils.getDate(document.getDocumentElement(), "date");
        assertThat(result)
                .isPresent()
                .get()
                .isEqualTo(LocalDate.of(2000, 4, 8));
    }

    private static Stream<Arguments> attrValueData() {
        return Stream.of(
                Arguments.of("<a b=\"c\"/>", "b", "c"),
                Arguments.of("<a/>", "b", "")
        );
    }

    @DisplayName("должен парсить детей элемента в список")
    @Test
    void shouldParseChildElementIntoList() {
        Document document = XMLDocumentUtils.getDocumentFromString("<root><a/><b attr=\"1\"/><b attr=\"2\"/></root>");
        HashMap<String, Function<Element, String>> map = new HashMap<>();
        map.put("a", e -> "1000");
        map.put("b", e -> e.getAttribute("attr"));
        List<String> result = XMLParseUtils.getChildList(document.getDocumentElement(), map);
        assertThat(result)
                .containsExactly("1000", "1", "2");
    }
}