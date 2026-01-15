package com.rakesh.Contact.Mnager.Forms;

import org.springframework.web.multipart.MultipartFile;

import com.rakesh.Contact.Manager.Validator.MaxFileSize;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserForm {

    @NotBlank(message = "Name is required")
    @Size(min=2, message="Name must be at least 2 characters long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min=6, message="Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "About is required")
    @Size(min=10, message="About must be at least 10 characters long")
    private String about;

    @NotBlank(message = "Phone number is required")
    @Size(min=10, max=10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @MaxFileSize(value = 2 * 1024 * 1024, message = "File size must not exceed 2 MB")
    private MultipartFile userImage;

    private String profilePic;
}
