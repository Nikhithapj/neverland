package com.example.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
//    @NotBlank(message = "Field cannot be blank")
//    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces ")
    private String name;
    @Column(name = "is_activated")
    private boolean activated;
    @Column(name = "is_deleted")
    private boolean deleted;

    public Category(String name) {
        this.name = name;
        this.activated = true;
        this.deleted = false;
    }
}