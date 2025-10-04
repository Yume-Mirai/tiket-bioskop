package com.uasjava.tiketbioskop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized API Response wrapper untuk React frontend integration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private List<T> dataList;
    private Map<String, Object> metadata;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;

    // Constructor untuk single data response
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Request berhasil diproses")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Constructor untuk list data response
    public static <T> ApiResponse<T> successList(List<T> dataList) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Data berhasil diambil")
                .dataList(dataList)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> successList(List<T> dataList, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .dataList(dataList)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Constructor untuk paginated response
    public static <T> ApiResponse<T> successPaginated(List<T> dataList, Map<String, Object> metadata) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Data berhasil diambil dengan pagination")
                .dataList(dataList)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Constructor untuk error response
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Constructor untuk created response
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Resource berhasil dibuat")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Constructor untuk updated response
    public static <T> ApiResponse<T> updated(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Resource berhasil diperbarui")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Constructor untuk deleted response
    public static ApiResponse<Void> deleted() {
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Resource berhasil dihapus")
                .timestamp(LocalDateTime.now())
                .build();
    }
}