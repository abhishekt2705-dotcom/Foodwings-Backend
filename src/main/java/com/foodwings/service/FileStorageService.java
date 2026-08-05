package com.foodwings.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction for persisting uploaded image files and returning their public path.
 */
public interface FileStorageService {

    /**
     * Stores the given file inside the configured uploads directory.
     *
     * @param file      the uploaded multipart file
     * @param subFolder logical sub-folder (e.g. "restaurants", "foods", "profiles")
     * @return the relative public path, e.g. {@code /uploads/foods/uuid.png}
     */
    String store(MultipartFile file, String subFolder);
}
