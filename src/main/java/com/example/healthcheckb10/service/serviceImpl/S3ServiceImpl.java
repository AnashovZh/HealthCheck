package com.example.healthcheckb10.service.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.example.healthcheckb10.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Component
public class S3ServiceImpl implements S3Service {
    @Autowired
    private  S3Client s3;
    @Value("${aws.bucket.url}")
    private String Bucket_Path;
    @Value("${aws.bucket.name}")
    private String NAME_PATH;
    private final AmazonS3 amazonS3;

    @Override
    public Map<String, String> uploadFile(MultipartFile file) throws IOException{
        String key = System.currentTimeMillis() + file.getOriginalFilename();
        PutObjectRequest putObjectAclRequest = PutObjectRequest.builder()
                .bucket(NAME_PATH)
                .contentType(file.getContentType())
                .key(key).build();
        s3.putObject(putObjectAclRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        log.info("Файл:%s успешно загружен на сервер.".formatted(key));
        return Map.of("link",Bucket_Path+key);
    }

    @Override
    public Map<String, String> deleteFile(String fileLink) {
        String key = fileLink.substring(fileLink.lastIndexOf("/") + 1);
        DeleteObjectRequest deleteObjectRequest=DeleteObjectRequest.builder()
                .bucket(NAME_PATH)
                .key(key).build();
        s3.deleteObject(deleteObjectRequest);
        log.info("Файл: %s успешно удален.".formatted(key));
        return Map.of("сообщение", "Файл успешно удален");
    }

    @Override
    public byte[] downloadFile(String fileName) {
        try {
            S3Object s3Object = amazonS3.getObject(NAME_PATH, fileName);
            S3ObjectInputStream objectContent = s3Object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке файла с S3");
        }
    }
}