package com.example.library.dto;

import com.example.library.model.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private Long id;
    @NotEmpty(message="Address Lines must be filled")
    @NotBlank
    private String address_line_1;
    @NotEmpty(message="Address Lines must be filled")
    @NotBlank
    private String address_line_2;
    @NotBlank
    @NotEmpty(message="City must not be null")
    private String city;
    @NotBlank
    @NotEmpty(message="pincode cannot be null")
    private String pincode;

    @NotEmpty(message="district cannot be null")
    @NotBlank
    private String district;
    @NotBlank
    @NotEmpty(message="state cannot be null")
    private String state;
    @NotBlank
    @NotEmpty(message="country must be filled")
    private String Country;

    private Customer customer;
    private boolean is_default;

}
