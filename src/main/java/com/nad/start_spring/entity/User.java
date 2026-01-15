package com.nad.start_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @ElementCollection(fetch = FetchType.EAGER)
    Set<String> roles;

    @OneToMany(mappedBy = "user")
    List<Loan> loans;

    @OneToMany(mappedBy = "user")
    List<Reservation> reservations;

    @OneToMany(mappedBy = "user")
    List<Notification> notifications;
}
