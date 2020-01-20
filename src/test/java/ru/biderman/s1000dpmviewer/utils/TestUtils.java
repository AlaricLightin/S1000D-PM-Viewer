package ru.biderman.s1000dpmviewer.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    private static final Path TEST_DATA_PATH = Paths.get("src/test/resources/data");

    public static Path getDataPath(String filename) {
        return TEST_DATA_PATH.resolve(filename);
    }

    public static File getDataFile(String filename) {
        return getDataPath(filename).toFile();
    }
}
