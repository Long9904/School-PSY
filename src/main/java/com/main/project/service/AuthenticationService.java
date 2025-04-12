package com.main.project.service;

import com.main.project.dto.request.LoginRequest;
import com.main.project.dto.request.RegisterRequest;
import com.main.project.dto.response.LoginResponse;
import com.main.project.entities.ParentStudent;
import com.main.project.entities.User;
import com.main.project.enums.UserRoleEnum;
import com.main.project.enums.UserStatusEnum;
import com.main.project.exception.AuthenticationException;
import com.main.project.exception.DuplicateException;
import com.main.project.exception.RelationshipException;
import com.main.project.exception.RoleException;
import com.main.project.repository.AuthenticationRepository;
import com.main.project.repository.ParentStudentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService implements UserDetailsService {


    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    public AuthenticationService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return authenticationRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    } // This method uses email instead of username because we store user email in JWT token

    public String register(RegisterRequest registerRequest) {
        // 1. Check duplicate email
        if (authenticationRepository.findUserByEmail(registerRequest.getEmail()).isPresent()) {
            throw new DuplicateException(List.of("Email already exists"));
        }

        // 2. Map basic user info & encode password
        User user = modelMapper.map(registerRequest, User.class);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRoleEnum.valueOf(registerRequest.getRole().toUpperCase()));

        // 3. Case A: Student
        if (registerRequest.getRole().equalsIgnoreCase("Student")) {
            User parent;
            Optional<User> existingParentOpt = authenticationRepository.findUserByEmail(registerRequest.getPartnerEmail());

            if (existingParentOpt.isPresent()) {
                parent = existingParentOpt.get();
                // Check nếu parent đã có student
                // uncomment this line if you want to check if the parent already has a student linked -> 1 parent - 1 student
//                if (parentStudentRepository.existsByParent(parent)) {
//                    throw new RelationshipException("Parent is already linked to another student.");
//                }

                if (parent.getRole() != UserRoleEnum.PARENT) {
                    throw new RoleException("Partner is register as a Student: " + parent.getEmail());
                }
            } else {
                parent = new User();
                parent.setFullName(registerRequest.getPartnerFullName());
                parent.setEmail(registerRequest.getPartnerEmail());
                parent.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // dùng lại password
                parent.setRole(UserRoleEnum.PARENT);
                parent.setAddress(registerRequest.getAddress());
                parent.setPhoneNumber(registerRequest.getPhoneNumber());
                parent = authenticationRepository.save(parent);
            }

            // Tạo liên kết ParentStudent
            ParentStudent parentStudent = new ParentStudent();
            parentStudent.setParent(parent);
            parentStudent.setStudent(user);
            user.setParentLink(parentStudent);

            // Lưu cả user (Student) và parent nếu có chỉnh sửa
            authenticationRepository.save(user);
            return "Student registered successfully and linked to parent: " + parent.getEmail();
        }

        // 4. Case B: Parent
        else if (registerRequest.getRole().equalsIgnoreCase("Parent")) {
            User student;
            Optional<User> existingStudentOpt = authenticationRepository.findUserByEmail(registerRequest.getPartnerEmail());

            if (existingStudentOpt.isPresent()) {
                student = existingStudentOpt.get();
                // Check nếu student đã có parent
                if (parentStudentRepository.existsByStudent(student)) {
                    throw new RelationshipException("Student is already linked to another parent.");
                }

                if (student.getRole() != UserRoleEnum.STUDENT) {
                    throw new RoleException("Partner is registered as a Parent: " + student.getEmail());
                }
            } else {
                student = new User();
                student.setFullName(registerRequest.getPartnerFullName());
                student.setEmail(registerRequest.getPartnerEmail());
                student.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                student.setRole(UserRoleEnum.STUDENT);
                student.setAddress(registerRequest.getAddress());
                student.setPhoneNumber(registerRequest.getPhoneNumber());
                student = authenticationRepository.save(student);
            }

            // Tạo liên kết ParentStudent
            ParentStudent parentStudent = new ParentStudent();
            parentStudent.setParent(user);
            parentStudent.setStudent(student);
            student.setParentLink(parentStudent);

            // Lưu cả user (Parent) và student nếu cần
            authenticationRepository.save(user);
            return "Parent registered successfully and linked to student: " + student.getEmail();
        }

        throw new RoleException("Unsupported role: " + registerRequest.getRole());
    }


    public LoginResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            User user = authenticationRepository.findUserByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
            if(user.getStatus().equals(UserStatusEnum.INACTIVE)){
                throw new AuthenticationException("Account is INACTIVE");
            }
            String token = tokenService.generateAccessToken(user);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setEmail(user.getEmail());
            loginResponse.setAccessToken(token);
            loginResponse.setRole(user.getRole());
            return loginResponse;
        }
        catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthenticationException("Invalid username or password");
        }
    }
}
