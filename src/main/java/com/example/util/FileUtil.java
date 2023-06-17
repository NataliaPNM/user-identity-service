package com.example.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUtil {

    private final ResourceLoader resourceLoader;

    public String getFileDataAsString(String classpath) {
//        "classpath:files/password-recovery-link.html"
        Resource resource = resourceLoader.getResource(classpath);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader =
                     new BufferedReader(
                             new InputStreamReader(
                                     resource.getInputStream(),
                                     Charset.forName(StandardCharsets.UTF_8.name())))) {
            int data = 0;
            while ((data = bufferedReader.read()) != -1) {
                char theChar = (char) data;
                stringBuilder.append(theChar);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }
}
