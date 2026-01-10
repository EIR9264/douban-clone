package com.douban.controller.admin;

import com.douban.service.ObjectStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/uploads")
public class AdminUploadController {

    private final ObjectStorageService storageService;

    public AdminUploadController(ObjectStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "prefix", required = false) String prefix) {
        ObjectStorageService.UploadedObject obj = storageService.upload(file, prefix);
        return Map.of("objectKey", obj.objectKey(), "url", obj.url());
    }

    @DeleteMapping
    public Map<String, Object> delete(@RequestParam("objectKey") String objectKey) {
        storageService.delete(objectKey);
        return Map.of("success", true);
    }
}

