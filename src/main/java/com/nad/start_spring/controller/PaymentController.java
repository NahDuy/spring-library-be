package com.nad.start_spring.controller;

import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.VNPayResponse;
import com.nad.start_spring.entity.Payment;
import com.nad.start_spring.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ApiResponse<VNPayResponse> pay(HttpServletRequest request) {
        return ApiResponse.<VNPayResponse>builder()
                .message("Payment created successfully")
                .status(paymentService.createVnPayPayment(request))
                .build();
    }

    @GetMapping("/vn-pay-callback")
    public ApiResponse<String> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return ApiResponse.<String>builder().status("Thanh toan thanh cong").build();
        } else {
            return ApiResponse.<String>builder()
                    .status("Failed")
                    .build();
        }

    }
}

