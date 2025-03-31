package com.main.project.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String fullName;

    private String email;

    private LocalDate dateOfBirth;

    private String gender;

    private String phoneNumber;

    private String address;
}
