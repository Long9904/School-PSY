package com.main.project.service;

import com.main.project.dto.request.RegisterRequest;
import com.main.project.entities.User;
import com.main.project.enums.UserRoleEnum;
import com.main.project.exception.DuplicateException;
import com.main.project.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {


    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private final ModelMapper modelMapper;


    public AuthenticationService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return authenticationRepository.findUserById(Long.parseLong(id))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    } // This method uses ID instead of username because we store user ID in JWT token

    public String register(RegisterRequest registerRequest) {
        // Check student email & parent email cannot be the same
        List<String> errors = new ArrayList<>();

        if (registerRequest.getStudentEmail().equals(registerRequest.getParentEmail())) {
            errors.add("Student email and parent email cannot be the same");
        }

        if (authenticationRepository.findUserByStudentEmail(registerRequest.getStudentEmail()).isPresent()) {
            errors.add("Student email already exists");
        }

        if (authenticationRepository.findUserByParentEmail(registerRequest.getParentEmail()).isPresent()) {
            errors.add("Parent email already exists");
        }

        if (!errors.isEmpty()) {
            throw new DuplicateException(errors); // custom exception nhận list
        }

        User user = modelMapper.map(registerRequest, User.class);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Set role by request type
        if (registerRequest.getRole().equalsIgnoreCase("student")) {
            user.setRole(UserRoleEnum.STUDENT);
        } else if (registerRequest.getRole().equalsIgnoreCase("parent")) {
            user.setRole(UserRoleEnum.PARENT);
        } else {
            return "Role not found";
        }
        authenticationRepository.save(user);
        return "Register successfully";
    }
}
