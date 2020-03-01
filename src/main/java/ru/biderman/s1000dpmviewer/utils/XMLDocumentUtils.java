package ru.biderman.s1000dpmviewer.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class XMLDocumentUtils {
    public static Document getDocumentFromStream(InputStream inputStream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(inputStream);
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document getDocumentFromString(String s) {
        if (s == null)
            return null;
        InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        return getDocumentFromStream(stream);
    }

    public static String getStringFromDocument(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch(TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }

}
