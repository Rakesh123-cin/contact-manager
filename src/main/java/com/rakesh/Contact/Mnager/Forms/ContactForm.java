package com.rakesh.Contact.Mnager.Forms;

import org.springframework.web.multipart.MultipartFile;

import com.rakesh.Contact.Manager.Validator.MaxFileSize;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ContactForm {

    @NotBlank(message = "Name is required")
    @Size(min=2, message="Name must be at least 2 characters long")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp="^\\d{10}$", message="Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Description is required")
    private String description;

    private boolean favorite;

    private String instagramLink;

    private String linkedInLink;

    @MaxFileSize(value = 2 * 1024 * 1024, message = "File size must not exceed 2 MB")
    private MultipartFile contactImage;

    private String picture;
}
