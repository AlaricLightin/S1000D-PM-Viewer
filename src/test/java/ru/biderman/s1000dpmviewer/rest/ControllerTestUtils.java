package ru.biderman.s1000dpmviewer.rest;

import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class ControllerTestUtils {
    static MockMultipartFile createTestMultipartFile(String content) throws IOException {
        InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return new MockMultipartFile("file", contentStream);
    }

}
