package com.microservices.instagrambackend.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.microservices.instagrambackend.config.Properties;
import com.microservices.instagrambackend.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseImageServiceImpl implements ImageService {

    private final Properties properties;

    @EventListener
    public void init(ApplicationReadyEvent event) {

        try {
            ClassPathResource serviceAccount = new ClassPathResource("firebase.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(properties.getBucketName())
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public String getImageUrl(String name) {
        return String.format(properties.getImageUrl(), name);
    }

    @Override
    public String save(MultipartFile file) throws IOException {

        ClassPathResource serviceAccount = new ClassPathResource("firebase.json");
        String name = generateFileName(file.getOriginalFilename());
        BlobInfo blobInfo = BlobInfo.newBuilder("fir-storage-e8631.appspot.com", name)
                .setContentType(file.getContentType())
                .build();
        byte[] content = file.getBytes();
        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build()
                .getService();
        Blob blob = storage.create(blobInfo, content);

        storage.createAcl(blob.getBlobId(), Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return name;
    }

}
