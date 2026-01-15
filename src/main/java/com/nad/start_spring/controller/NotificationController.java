package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.NotificationRequest;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.NotificationResponse;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.service.NotificationService;
import com.nad.start_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;
    
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        
        List<NotificationResponse> notifications = 
            notificationService.getUserNotifications(user)
                .stream()
                .map(n -> NotificationResponse.builder()
                    .id(n.getId())
                    .title(n.getTitle())
                    .message(n.getMessage())
                    .type(n.getType())
                    .isRead(n.getIsRead())
                    .createdAt(n.getCreatedAt())
                    .loanDetailId(n.getLoanDetail() != null ? n.getLoanDetail().getLoanDetailId() : null)
                    .fineId(n.getFine() != null ? n.getFine().getFineId() : null)
                    .build())
                .toList();
        
        return ApiResponse.<List<NotificationResponse>>builder()
            .status(notifications)
            .build();
    }
    
    @PostMapping
    public ApiResponse<Void> createNotification(@RequestBody NotificationRequest request) {
        try {
            notificationService.createNotificationByAdmin(request);
            return ApiResponse.<Void>builder()
                .message("Tạo thông báo và gửi email thành công")
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<Void>builder()
                .message("Lỗi: " + e.getMessage())
                .build();
        }
    }
    
    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        
        List<NotificationResponse> notifications = 
            notificationService.getUnreadNotifications(user)
                .stream()
                .map(n -> NotificationResponse.builder()
                    .id(n.getId())
                    .title(n.getTitle())
                    .message(n.getMessage())
                    .type(n.getType())
                    .isRead(n.getIsRead())
                    .createdAt(n.getCreatedAt())
                    .loanDetailId(n.getLoanDetail() != null ? n.getLoanDetail().getLoanDetailId() : null)
                    .fineId(n.getFine() != null ? n.getFine().getFineId() : null)
                    .build())
                .toList();
        
        return ApiResponse.<List<NotificationResponse>>builder()
            .status(notifications)
            .build();
    }
    
    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.<Void>builder()
            .message("Đánh dấu thông báo là đã đọc")
            .build();
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ApiResponse.<Void>builder()
            .message("Xóa thông báo thành công")
            .build();
    }
}