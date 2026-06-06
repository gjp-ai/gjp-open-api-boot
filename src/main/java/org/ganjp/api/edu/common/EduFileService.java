package org.ganjp.api.edu.common;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EduFileService {
    public File resolveExistingFile(String directory, String filename) {
        if (directory == null || directory.isBlank() || filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("File not found");
        }
        Path path = Path.of(directory, filename);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File not found: " + filename);
        }
        return path.toFile();
    }
}
