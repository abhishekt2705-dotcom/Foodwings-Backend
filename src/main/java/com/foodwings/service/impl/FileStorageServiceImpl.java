package com.foodwings.service.impl;

import com.foodwings.exception.BadRequestException;
import com.foodwings.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Stores uploaded images on the local filesystem under the configured uploads directory.
 * Only the relative image path is persisted in the database.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif");

    private final Path root;

    public FileStorageServiceImpl(@Value("${foodwings.file.upload-dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, String subFolder) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file must not be empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only image files (png, jpg, jpeg, webp, gif) are allowed");
        }

        try {
            Path targetDir = root.resolve(subFolder).normalize();
            Files.createDirectories(targetDir);

            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
            String extension = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                extension = original.substring(dot);
            }
            String filename = UUID.randomUUID() + extension;
            Path destination = targetDir.resolve(filename);

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + subFolder + "/" + filename;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store uploaded file: " + e.getMessage());
        }
    }
}
