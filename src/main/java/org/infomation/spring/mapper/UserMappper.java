package org.infomation.spring.mapper;

import org.infomation.spring.dto.request.UserCreationRequest;
import org.infomation.spring.dto.request.UserUpdateRequest;
import org.infomation.spring.dto.response.UserResponse;
import org.infomation.spring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMappper {
    User toUser(UserCreationRequest userCreateRequest);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
