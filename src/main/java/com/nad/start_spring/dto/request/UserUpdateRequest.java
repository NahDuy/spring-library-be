package com.nad.start_spring.dto.request;

import com.nad.start_spring.validator.DobConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    String password;
    String name;
    String email;
    String address;
    Date joinDate;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;

}
