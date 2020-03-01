package ru.biderman.s1000dpmviewer.testutils;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.biderman.s1000dpmviewer.utils.XMLDocumentUtils.getDocumentFromStream;

public class TestUtils {
    private static final Path TEST_DATA_PATH = Paths.get("src/test/resources/data");

    public static Path getDataPath(String filename) {
        return TEST_DATA_PATH.resolve(filename);
    }

    public static File getDataFile(String filename) {
        return getDataPath(filename).toFile();
    }

    public static Document getDocumentFromFile(File file) {
        try(
                FileInputStream fis = new FileInputStream(file)
        )
        {
            return getDocumentFromStream(fis);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
