package com.douban.service;

import com.douban.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

@Service
public class ObjectStorageService {

    private final MinioProperties props;
    private final MinioClient client;

    public ObjectStorageService(MinioProperties props) {
        this.props = props;
        this.client = MinioClient.builder()
                .endpoint(props.getEndpoint())
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }

    public UploadedObject upload(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        ensureBucket();
        String cleanPrefix = sanitizePrefix(prefix);
        String objectKey = buildObjectKey(cleanPrefix, file.getOriginalFilename());
        try (InputStream in = file.getInputStream()) {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.getBucket())
                            .object(objectKey)
                            .stream(in, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return new UploadedObject(objectKey, publicUrl(objectKey));
        } catch (Exception e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    public void delete(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) return;
        ensureBucket();
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(props.getBucket()).object(objectKey).build());
        } catch (Exception e) {
            throw new RuntimeException("删除失败: " + e.getMessage());
        }
    }

    private void ensureBucket() {
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(props.getBucket()).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(props.getBucket()).build());
            }
            if (props.isPublicRead()) {
                ensurePublicReadPolicy();
            }
        } catch (Exception e) {
            throw new RuntimeException("MinIO 不可用: " + e.getMessage());
        }
    }

    private void ensurePublicReadPolicy() {
        try {
            String bucket = props.getBucket();
            String policy = """
                    {
                      "Version": "2012-10-17",
                      "Statement": [
                        {
                          "Effect": "Allow",
                          "Principal": { "AWS": ["*"] },
                          "Action": ["s3:GetObject"],
                          "Resource": ["arn:aws:s3:::%s/*"]
                        }
                      ]
                    }
                    """.formatted(bucket);
            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
        } catch (Exception ignored) {
            // 忽略：如果没有权限设置 policy，仍可通过预签名 URL 等方式访问
        }
    }

    private String sanitizePrefix(String prefix) {
        if (prefix == null) return "";
        String p = prefix.trim().replace("\\", "/");
        while (p.startsWith("/")) p = p.substring(1);
        p = p.replace("..", "");
        if (p.isBlank()) return "";
        if (!p.endsWith("/")) p += "/";
        return p;
    }

    private String buildObjectKey(String prefix, String originalFilename) {
        String name = (originalFilename == null ? "" : originalFilename.trim()).replace("\\", "_").replace("/", "_");
        String safeName = name.isBlank() ? "file" : name;
        return prefix + UUID.randomUUID() + "-" + safeName;
    }

    private String publicUrl(String objectKey) {
        String base = props.getPublicEndpoint();
        if (base == null || base.isBlank()) base = props.getEndpoint();
        String normalized = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        // publicEndpoint like http://localhost:9000
        URI uri = URI.create(normalized + "/" + props.getBucket() + "/" + objectKey);
        return uri.toString();
    }

    public record UploadedObject(String objectKey, String url) {}
}
