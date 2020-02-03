package ru.biderman.s1000dpmviewer.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLParseUtils {
    public static Optional<Element> getFirstChildElement(Element parentElement, String name) {
        if(parentElement == null || name == null || name.isEmpty())
            return Optional.empty();

        Element result = null;
        NodeList nodeList = parentElement.getElementsByTagName(name);
        if (nodeList.getLength() > 0)
            result = (Element) nodeList.item(0);

        return Optional.ofNullable(result);
    }

    public static String getTextFromChildElement(Element parentElement, String name) {
        return getFirstChildElement(parentElement, name)
                .map(Element::getTextContent)
                .orElse("");
    }

    public static Optional<Element> getElement(Node rootNode, String path) {
        if(rootNode == null || path == null || path.isEmpty())
            return Optional.empty();

        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            Node node = (Node) xpath.evaluate(path, rootNode, XPathConstants.NODE);
            return node instanceof Element ? Optional.of((Element) node) : Optional.empty();
        }
        catch (XPathExpressionException e) {
            // TODO определиться с логированием и обработкой исключений
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static String getTextFromElement(Node rootNode, String path) {
        return getElement(rootNode, path)
                .map(Element::getTextContent)
                .orElse("");
    }

    public static Optional<String> getDelimitedTextFromAttrs(Element parentElement, String elementName, String... attributeNames) {
        return getFirstChildElement(parentElement, elementName)
                .map(element -> Stream.of(attributeNames)
                        .map(element::getAttribute)
                        .map(attribute -> attribute != null ? attribute : "")
                        .collect(Collectors.joining("-")));
    }

    public static Optional<LocalDate> getDate(Element parentElement, String elementName) {
        return getFirstChildElement(parentElement, elementName)
                .flatMap(element -> {
                    try {
                        return Optional.of(LocalDate.of(
                                Integer.parseInt(element.getAttribute("year")),
                                Integer.parseInt(element.getAttribute("month")),
                                Integer.parseInt(element.getAttribute("day"))
                        ));
                    }
                    catch (NumberFormatException| DateTimeException e) {
                        return Optional.empty();
                    }
                });
    }

    /**
     * Парсит детей родительского элемента и собирает результат в список
     * @param parentElement - родительский элемент
     * @param functionMap - словарь функций, где ключ - имя дочернего xml-элемента, а значение - функция, которая из него делает элемент списка
     * @param <T> - тип элемента результируюшего списка
     */
    public static <T> List<T> getChildList(Element parentElement, Map<String, Function<Element,T>> functionMap) {
        assert parentElement != null;
        NodeList nodeList = parentElement.getChildNodes();
        ArrayList<T> result = new ArrayList<>();
        for(int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                Function<Element, T> function = functionMap.get(element.getTagName());
                if (function != null) {
                    T resultElement = function.apply(element);
                    if (resultElement != null)
                        result.add(resultElement);
                }
            }
        }
        return result;
    }
}
