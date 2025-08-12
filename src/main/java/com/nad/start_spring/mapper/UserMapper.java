package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.UserCreateRequest;
import com.nad.start_spring.dto.response.UserResponse;
import com.nad.start_spring.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreateRequest userCreateRequest);
    UserResponse toUserResponse(User user);


}
