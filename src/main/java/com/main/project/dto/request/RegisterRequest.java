package com.main.project.dto.request;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Size(min = 1, max = 30, message = "Full name must be between 1 and 30 characters")
    private String fullName;

    @Email(message = "Email should be valid")
    private String email;

    private String password;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Invalid date of birth")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be 'Male', 'Female', or 'Other'")
    @NotBlank(message = "Gender is required")
    private String gender;

    @Pattern(regexp = "(84|0[35789])[0-9]{8}", message = "Invalid phone number")
    private String phoneNumber;

    @Size(min = 0 ,max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Pattern(regexp = "^(Student|Parent|Psychologist)$", message = "Role must be 'Student', 'Parent', or 'Psychologist'")
    private String role;
}
