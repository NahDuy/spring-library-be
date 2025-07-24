package com.nad.start_spring.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;
     String name;
     String email;
     String username;
     String password;
     String address;
     Date joinDate;

    @OneToMany(mappedBy = "user")
    List<Loan> loans;

    @OneToMany(mappedBy = "user")
    List<Reservation> reservations;

}
