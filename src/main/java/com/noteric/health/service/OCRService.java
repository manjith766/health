package com.noteric.health.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class OCRService {
    private static final String UPLOAD_DIR = "uploads";
    // IMPORTANT: This must be the full path to the tessdata folder itself!
    private static final String TESSDATA_PATH = "C:/Program Files/Tesseract-OCR/tessdata";

    public String extractTextFromFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String ext = file.getOriginalFilename() != null
                ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'))
                : "";
        Path filePath = uploadPath.resolve(UUID.randomUUID().toString() + ext);
        Files.copy(file.getInputStream(), filePath);

        return performOCR(filePath.toFile());
    }

    private String performOCR(File file) {
        try {
            ITesseract tesseract = new Tesseract();
            // Direct path to the tessdata folder
            tesseract.setDatapath(TESSDATA_PATH);
            tesseract.setLanguage("eng");
            String result = tesseract.doOCR(file);
            log.info("OCR extraction successful: {}", file.getAbsolutePath());
            return result;
        } catch (Exception e) {
            log.error("Error during OCR: {}", e.getMessage());
            throw new RuntimeException("Failed to extract text", e);
        }
    }
}
