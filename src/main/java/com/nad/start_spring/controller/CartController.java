package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.AddToCartRequest;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.CartResponse;
import com.nad.start_spring.service.CartService;
import com.nad.start_spring.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;
    UserService userService;

    @GetMapping("/my-cart")
    public ApiResponse<CartResponse> getMyCart() {
        String userId = userService.myInfo().getId();
        return ApiResponse.<CartResponse>builder()
                .status(cartService.getCart(userId))
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<CartResponse> addToCart(@RequestBody AddToCartRequest request) {
        String userId = userService.myInfo().getId();
        return ApiResponse.<CartResponse>builder()
                .status(cartService.addToCart(userId, request))
                .build();
    }

    @DeleteMapping("/item/{itemId}")
    public ApiResponse<CartResponse> removeFromCart(@PathVariable String itemId) {
        String userId = userService.myInfo().getId();
        return ApiResponse.<CartResponse>builder()
                .status(cartService.removeFromCart(userId, itemId))
                .build();
    }

    @PutMapping("/item/{itemId}")
    public ApiResponse<CartResponse> updateQuantity(
            @PathVariable String itemId,
            @RequestParam int quantity) {
        String userId = userService.myInfo().getId();
        return ApiResponse.<CartResponse>builder()
                .status(cartService.updateQuantity(userId, itemId, quantity))
                .build();
    }

    @DeleteMapping
    public ApiResponse<String> clearCart() {
        String userId = userService.myInfo().getId();
        cartService.clearCart(userId);
        return ApiResponse.<String>builder()
                .status("Cart cleared successfully")
                .build();
    }

    @PostMapping("/checkout")
    public ApiResponse<String> checkout() {
        String userId = userService.myInfo().getId();
        return ApiResponse.<String>builder()
                .status(cartService.checkout(userId))
                .build();
    }
}
