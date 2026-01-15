package com.nad.start_spring.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String userId;
    private String title;
    private String message;
    private String type;
    private String loanDetailId;
    private String fineId;
}
