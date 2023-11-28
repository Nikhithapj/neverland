package com.example.library.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;
//    @NotBlank(message = "Field cannot be blank")
//    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    private int currentQuantity;
    private double costPrice;
    private double salePrice;
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<Image> images;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;
    private boolean is_activated;
    private boolean is_deleted;


    @OneToMany(fetch=FetchType.EAGER,cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH})
    @JoinTable(name="product_sizes",joinColumns = @JoinColumn(name="product_id",referencedColumnName="product_id"),
    inverseJoinColumns = @JoinColumn(name="size_id",referencedColumnName = "size_id"))
    private List<Size> sizes;

    @ElementCollection
    @CollectionTable(name="product_image_urls",joinColumns = @JoinColumn(name="product_id"))
    private List<String>imageUrls;

}
