package ru.gnivc.driverservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gnivc.driverservice.service.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("driver/v1/image")
public class ImageController {
    private final ImageService imageService;

    @PostMapping()
    public ResponseEntity<String> uploadImage(
            @RequestBody MultipartFile file,
            @RequestParam long taskId,
            @RequestParam long tripId,
            @RequestParam String companyName) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("File is empty", HttpStatus.BAD_REQUEST);
        } else {
            String resultFileName = companyName + "/" + taskId + "/" + tripId + "/" + file.getOriginalFilename();
            return imageService.uploadImage(resultFileName, file, companyName, tripId);
        }
    }

    @GetMapping(
            produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(
            @RequestParam long taskId,
            @RequestParam long tripId,
            @RequestParam String fileName,
            @RequestParam String companyName) {

        String imageName = companyName + "/" + taskId + "/" + tripId + "/" + fileName;
        return imageService.downloadImage(imageName, companyName, tripId);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeImage(@RequestParam long taskId,
                                            @RequestParam long tripId,
                                            @RequestParam String fileName,
                                            @RequestParam String companyName) {
        String imageName = companyName + "/" + taskId + "/" + tripId + "/" + fileName;
        return imageService.removeImage(imageName);
    }
}
