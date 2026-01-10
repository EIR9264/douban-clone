package com.douban.config;

import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        FieldError fe = e.getBindingResult().getFieldError();
        String msg = fe != null ? fe.getDefaultMessage() : "参数错误";
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "参数错误";
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<Map<String, String>> handleBinding(ServletRequestBindingException e) {
        return ResponseEntity.status(401).body(Map.of("error", "未登录或登录已过期"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccess(DataAccessException e) {
        String msg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        if (msg == null || msg.isBlank()) msg = "数据库错误";
        return ResponseEntity.status(500).body(Map.of("error", msg));
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
    public ResponseEntity<Map<String, String>> handleUpload(Exception e) {
        return ResponseEntity.status(413).body(Map.of("error", "上传文件过大或格式错误"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isBlank()) msg = "服务器内部错误";
        return ResponseEntity.status(500).body(Map.of("error", msg));
    }
}
