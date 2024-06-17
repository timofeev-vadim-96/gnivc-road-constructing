package ru.gnivc.driverservice.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getAddress(), properties.getPort(), properties.isTsl())
                .credentials(properties.getLogin(), properties.getPassword())
                .build();
    }
}
