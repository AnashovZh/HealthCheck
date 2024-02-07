package com.example.healthcheckb10.api;

import com.example.healthcheckb10.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/files")
@Tag(name = "S3Client", description = "S3 upload")
public class S3Api {
    private final S3Service s3Service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Метод для загрузки файла",
               description = "Вы можете загрузить файлы.")
    private Map<String, String> uploadFile(@RequestParam MultipartFile file) throws IOException {
        return s3Service.uploadFile(file);
    }

    @DeleteMapping
    @Operation(summary = "Метод для удаления файла.",
            description = "Удалить файл из сервера.")
    public Map<String, String> deleteFile(@RequestParam String fileLink) {
        return s3Service.deleteFile(fileLink);
    }

    @GetMapping
    @Operation(summary = "Метод для загрузки файла с бакета",
               description = "Эта операция позволяет загрузить файл из указанной корзины S3.")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) {
        byte[] fileData = s3Service.downloadFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
    }
}