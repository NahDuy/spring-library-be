package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.AddToCartRequest;
import com.nad.start_spring.dto.response.CartItemResponse;
import com.nad.start_spring.dto.response.CartResponse;
import com.nad.start_spring.entity.*;
import com.nad.start_spring.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
        CartRepository cartRepository;
        CartItemRepository cartItemRepository;
        UserRepository userRepository;
        BookRepository bookRepository;
        LoanRepository loanRepository;
        LoanDetailRepository loanDetailRepository;

        @Transactional
        public CartResponse getCart(String userId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseGet(() -> createCartForUser(userId));
                return mapToCartResponse(cart);
        }

        @Transactional
        public CartResponse addToCart(String userId, AddToCartRequest request) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseGet(() -> createCartForUser(userId));

                Book book = bookRepository.findById(request.getBookId())
                                .orElseThrow(() -> new RuntimeException("Book not found"));

                Optional<CartItem> existingItem = cart.getCartItems().stream()
                                .filter(item -> item.getBook().getBookID().equals(book.getBookID()))
                                .findFirst();

                if (existingItem.isPresent()) {
                        CartItem item = existingItem.get();
                        item.setQuantity(item.getQuantity() + request.getQuantity());
                        cartItemRepository.save(item);
                } else {
                        CartItem newItem = CartItem.builder()
                                        .cart(cart)
                                        .book(book)
                                        .quantity(request.getQuantity())
                                        .build();
                        cart.getCartItems().add(newItem);
                        cartItemRepository.save(newItem);
                }

                return mapToCartResponse(cartRepository.save(cart));
        }

        @Transactional
        public CartResponse removeFromCart(String userId, String itemId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                cart.getCartItems().removeIf(item -> item.getId().equals(itemId));
                cartRepository.save(cart);

                return mapToCartResponse(cart);
        }

        @Transactional
        public CartResponse updateQuantity(String userId, String itemId, int quantity) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                cart.getCartItems().stream()
                                .filter(item -> item.getId().equals(itemId))
                                .findFirst()
                                .ifPresent(item -> {
                                        item.setQuantity(quantity);
                                        cartItemRepository.save(item);
                                });

                return mapToCartResponse(cartRepository.save(cart));
        }

        @Transactional
        public void clearCart(String userId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));
                cart.getCartItems().clear();
                cartRepository.save(cart);
        }

        @Transactional
        public String checkout(String userId) {
                Cart cart = cartRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Cart not found"));

                if (cart.getCartItems().isEmpty()) {
                        throw new RuntimeException("Cart is empty");
                }

                // Create Loan
                Loan loan = Loan.builder()
                                .user(cart.getUser())
                                .startDate(LocalDate.now())
                                .status("CONFIRMED")
                                .build();
                loan = loanRepository.save(loan);

                // Create LoanDetails
                List<LoanDetail> loanDetails = new ArrayList<>();
                for (CartItem item : cart.getCartItems()) {
                        LoanDetail detail = LoanDetail.builder()
                                        .loan(loan)
                                        .book(item.getBook())
                                        .quantity(item.getQuantity())
                                        .dueDate(LocalDate.now().plusWeeks(2))
                                        .status("BORROWED")
                                        .build();
                        loanDetails.add(detail);
                }
                loanDetailRepository.saveAll(loanDetails);

                // Clear Cart
                cart.getCartItems().clear();
                cartRepository.save(cart);

                return loan.getLoanId(); // Return Loan ID
        }

        private Cart createCartForUser(String userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Cart cart = Cart.builder().user(user).cartItems(new ArrayList<>()).build();
                return cartRepository.save(cart);
        }

        private CartResponse mapToCartResponse(Cart cart) {
                List<CartItemResponse> items = cart.getCartItems().stream()
                                .map(item -> CartItemResponse.builder()
                                                .id(item.getId())
                                                .bookId(item.getBook().getBookID())
                                                .bookTitle(item.getBook().getTitle())
                                                .bookImage(
                                                                item.getBook().getImageUrls().isEmpty() ? null
                                                                                : item.getBook().getImageUrls().get(0))
                                                .quantity(item.getQuantity())
                                                .build())
                                .collect(Collectors.toList());

                return CartResponse.builder()
                                .id(cart.getId())
                                .userId(cart.getUser().getId())
                                .items(items)
                                .totalItems(items.stream().mapToInt(CartItemResponse::getQuantity).sum())
                                .build();
        }
}
