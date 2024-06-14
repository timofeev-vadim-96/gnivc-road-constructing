package ru.gnivc.driverservice.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.driverservice.dto.input.TaskDto;
import ru.gnivc.driverservice.dto.input.TripDto;
import ru.gnivc.driverservice.provider.LogistProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService {
    private final MinioClient minioClient;
    private final LogistProvider logistProvider;
    private static final String BUCKET_NAME = "gnivc-bucket";


    public ResponseEntity<String> uploadImage(String resultFileName, MultipartFile file, String companyName, long tripId) {
        TripDto tripById = logistProvider.getTripById(tripId, companyName);
        log.info("trip received from logist-ms: " + tripById);
        String answer = "There is not trip with id = " + tripId + " and companyName = " + companyName;
        if (tripById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        }
        return putObjectToBucket(file, resultFileName);
    }

    public ResponseEntity<byte[]> downloadImage(String resultFileName, String companyName, long tripId) {
        TripDto tripById = logistProvider.getTripById(tripId, companyName);
        String answer = "There is not trip with id = " + tripId + " and companyName = " + companyName;
        if (tripById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, answer);
        }
        return getObjectFromBucket(resultFileName);
    }

    public ResponseEntity<byte[]> getObjectFromBucket(String objectName) {
        log.info("image to search: " + objectName);
        try (InputStream stream =
                     minioClient.getObject(GetObjectArgs
                             .builder()
                             .bucket(BUCKET_NAME)
                             .object(objectName)
                             .build())) {
            byte[] image = IOUtils.toByteArray(stream);
            return new ResponseEntity<>(image, HttpStatus.OK);
        } catch (Exception e) {
            log.info("словили исключение");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private void removeBucket(String bucketName) {
        RemoveBucketArgs build = RemoveBucketArgs.builder()
                .bucket(bucketName)
                .build();
    }

    private void createBucketIfNotExists(String name) {
        try {
            boolean isAlreadyExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(name)
                            .build());
            if (isAlreadyExists) {
                log.info("Trying to create new Minio bucket. Bucket with name: " + name + " is already exists!");
            } else {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(name)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Minio: " + e.getMessage());
        }
    }

    private ResponseEntity<String> putObjectToBucket(MultipartFile image, String resultFileName) {
        createBucketIfNotExists(BUCKET_NAME);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image.getBytes())) {
            log.info("зашли в блок трай");
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket(BUCKET_NAME)
                    .object(resultFileName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            return new ResponseEntity<>(
                    "The image has been uploaded successfully with name: " + resultFileName,
                    HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("словили исключение");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
