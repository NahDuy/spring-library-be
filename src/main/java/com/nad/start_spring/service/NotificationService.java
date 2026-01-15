package com.nad.start_spring.service;

import com.nad.start_spring.entity.*;
import com.nad.start_spring.enums.NotificationType;
import com.nad.start_spring.dto.request.NotificationRequest;
import com.nad.start_spring.repository.NotificationRepository;
import com.nad.start_spring.repository.UserRepository;
import com.nad.start_spring.repository.LoanDetailRepository;
import com.nad.start_spring.repository.FineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final LoanDetailService loanDetailService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final LoanDetailRepository loanDetailRepository;
    private final FineRepository fineRepository;
    
    private static final int DAYS_BEFORE_DUE = 3; // Thông báo 3 ngày trước hạn
    
    /**
     * Tạo thông báo sắp đến hạn trả sách
     */
    public void createDueSoonNotification(LoanDetail loanDetail) {
        User user = loanDetail.getLoan().getUser();
        
        Notification notification = Notification.builder()
            .user(user)
            .title("Sắp đến hạn trả sách")
            .message("Sách \"" + loanDetail.getBook().getTitle() + 
                     "\" sẽ đến hạn trả vào ngày " + loanDetail.getDueDate())
            .type(NotificationType.LOAN_DUE_SOON)
            .loanDetail(loanDetail)
            .build();
        
        notificationRepository.save(notification);
        emailService.sendNotification(user.getEmail(), notification.getTitle(), notification.getMessage());
    }
    
    /**
     * Tạo thông báo quá hạn trả sách
     */
    public void createOverdueNotification(LoanDetail loanDetail) {
        User user = loanDetail.getLoan().getUser();
        
        Notification notification = Notification.builder()
            .user(user)
            .title("Quá hạn trả sách")
            .message("Sách \"" + loanDetail.getBook().getTitle() + 
                     "\" đã quá hạn trả. Vui lòng trả sách ngay.")
            .type(NotificationType.LOAN_OVERDUE)
            .loanDetail(loanDetail)
            .build();
        
        notificationRepository.save(notification);
        emailService.sendNotification(user.getEmail(), notification.getTitle(), notification.getMessage());
    }
    
    /**
     * Tạo thông báo nộp phạt
     */
    public void createFineNotification(Fine fine) {
        User user = fine.getUser();
        
        Notification notification = Notification.builder()
            .user(user)
            .title("Thông báo phạt")
            .message("Bạn có khoản phạt " + fine.getAmount() + " VND. " +
                     "Vui lòng nộp phạt trước ngày " + fine.getDueDate())
            .type(NotificationType.FINE_NOTICE)
            .fine(fine)
            .build();
        
        notificationRepository.save(notification);
        emailService.sendNotification(user.getEmail(), notification.getTitle(), notification.getMessage());
    }
    
    /**
     * Chạy tự động mỗi ngày lúc 8 AM để kiểm tra các sách sắp đến hạn
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkLoansAndSendNotifications() {
        LocalDate upcomingDueDate = LocalDate.now().plusDays(DAYS_BEFORE_DUE);
        
        // Lấy các LoanDetail sắp đến hạn
        List<LoanDetail> upcomingLoans = loanDetailService.findByDueDate(upcomingDueDate);
        upcomingLoans.forEach(this::createDueSoonNotification);
        
        // Lấy các LoanDetail đã quá hạn
        List<LoanDetail> overdueLoans = loanDetailService.findOverdueLoans();
        overdueLoans.forEach(this::createOverdueNotification);
    }
    
    /**
     * Lấy tất cả thông báo của user
     */
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    /**
     * Lấy các thông báo chưa đọc
     */
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user);
    }
    
    /**
     * Đánh dấu thông báo là đã đọc
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }
    
    /**
     * Xóa thông báo
     */
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Tạo thông báo từ admin API (cho phép test email)
     */
    public void createNotificationByAdmin(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        LoanDetail loanDetail = null;
        if (request.getLoanDetailId() != null) {
            loanDetail = loanDetailRepository.findById(request.getLoanDetailId())
                .orElse(null);
        }
        
        Fine fine = null;
        if (request.getFineId() != null) {
            fine = fineRepository.findById(request.getFineId())
                .orElse(null);
        }
        
        NotificationType type = NotificationType.valueOf(request.getType());
        
        Notification notification = Notification.builder()
            .user(user)
            .title(request.getTitle())
            .message(request.getMessage())
            .type(type)
            .loanDetail(loanDetail)
            .fine(fine)
            .build();
        
        notificationRepository.save(notification);
        emailService.sendNotification(user.getEmail(), notification.getTitle(), notification.getMessage());
    }
}