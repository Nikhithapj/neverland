package com.example.library.dto;


import com.example.library.model.Category;
import com.example.library.model.Image;
import com.example.library.model.Size;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long id;
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")

    @NotBlank(message = "Field cannot be blank")
    private String name;

//    @NotBlank(message = "Field cannot be blank")
    private String description;
//    @Min(value = 0, message = "Current quantity must be greater than or equal to 0")


    private int currentQuantity;
//    @Positive(message = "Cost price must be a positive number")

    private double costPrice;
//    @Positive(message = "Cost price must be a positive number")

    private double salePrice;

    private List<Image> images;
//    @NotNull(message = "Category is required")
    private Category category;

    private boolean activated;

    private List<Size> sizes;


    private boolean deleted;


}
