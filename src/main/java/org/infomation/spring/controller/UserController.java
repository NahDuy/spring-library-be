package org.infomation.spring.controller;

import lombok.Getter;
import org.infomation.spring.dto.request.UserCreationRequest;
import org.infomation.spring.dto.request.UserUpdateRequest;
import org.infomation.spring.dto.response.UserResponse;
import org.infomation.spring.entity.User;
import org.infomation.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    UserResponse createUser(@RequestBody UserCreationRequest user) {
        return userService.createUser(user);
    }
    @GetMapping
    List<UserResponse> getAllUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String id) {
        return userService.getUser(id);
    }
    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
