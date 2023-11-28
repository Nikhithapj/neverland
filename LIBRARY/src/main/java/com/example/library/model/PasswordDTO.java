package com.example.library.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class PasswordDTO {

    private long id;
    @jakarta.validation.constraints.Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    @NotBlank(message = "Can not be empty")
    private String password;
    @Size(min=5, max=30,message = "Minimum 5-30 charaters required")
    @NotBlank(message = "Can not be empty")
    private String confirmPassword;
}
