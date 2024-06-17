package ru.gnivc.driverservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ResponseEntity<String> uploadImage(String resultFileName, MultipartFile file, String companyName, long tripId);

    ResponseEntity<byte[]> downloadImage(String resultFileName, String companyName, long tripId);

    ResponseEntity<Void> removeImage(String objectName);
}
