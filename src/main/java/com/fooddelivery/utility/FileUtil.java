package com.fooddelivery.utility;

import com.fooddelivery.exception.DataAccessException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public final class FileUtil {
    private FileUtil() {
        // Prevent instantiation
    }

    public static List<String> readAllLines(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                return Collections.emptyList();
            }
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataAccessException("Unable to read file: " + filePath, e);
        }
    }

    public static void writeAllLines(String filePath, List<String> lines) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataAccessException("Unable to write file: " + filePath, e);
        }
    }
}
