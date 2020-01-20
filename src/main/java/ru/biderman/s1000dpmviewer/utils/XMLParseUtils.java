package ru.biderman.s1000dpmviewer.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLParseUtils {
    public static Element getFirstChildElement(Element parentElement, String name) {
        if(parentElement == null || name == null || name.isEmpty())
            return null;

        Element result = null;
        NodeList nodeList = parentElement.getElementsByTagName(name);
        if (nodeList.getLength() > 0)
            result = (Element) nodeList.item(0);

        return result;
    }

    public static Element getElement(Node rootNode, String path) {
        if(rootNode == null || path == null || path.isEmpty())
            return null;

        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            Node node = (Node) xpath.evaluate(path, rootNode, XPathConstants.NODE);
            return node instanceof Element ? (Element) node : null;
        }
        catch (XPathExpressionException e) {
            // TODO определиться с логированием и обработкой исключений
            e.printStackTrace();
            return null;
        }
    }

    public static String getDelimitedTextFromAttrs(Element parentElement, String elementName, String... attributeNames) {
        Element element = getFirstChildElement(parentElement, elementName);
        if (element != null) {
            return Stream.of(attributeNames)
                    .map(element::getAttribute)
                    .map(attribute -> attribute != null ? attribute : "")
                    .collect(Collectors.joining("-"));
        }
        else
            return null;
    }

}
