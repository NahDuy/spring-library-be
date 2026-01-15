package com.nad.start_spring.repository;

import com.nad.start_spring.entity.Notification;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndIsReadFalse(User user);
    List<Notification> findByTypeAndIsReadFalse(NotificationType type);
}