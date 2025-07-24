package com.nad.start_spring.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Category {
    @Id
    private String categoryId;

    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Book> books;
}
