package com.wastemanagement.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Files.createDirectories(uploadPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = extractExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/images/" + uniqueFilename;
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded image", ex);
        }
    }

    public int deleteAllStoredImages() {
        try {
            if (!Files.exists(uploadPath) || !Files.isDirectory(uploadPath)) {
                return 0;
            }
            try (Stream<Path> paths = Files.list(uploadPath)) {
                return (int) paths
                        .filter(Files::isRegularFile)
                        .mapToLong(p -> {
                            try {
                                Files.deleteIfExists(p);
                                return 1L;
                            } catch (IOException ignored) {
                                return 0L;
                            }
                        })
                        .sum();
            }
        } catch (IOException ex) {
            return 0;
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
