package com.main.project.service;

import com.main.project.dto.request.RegisterRequest;
import com.main.project.entities.User;
import com.main.project.enums.UserRoleEnum;
import com.main.project.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

    public String register(RegisterRequest registerRequest) {
        // Check duplicate

        User user = modelMapper.map(registerRequest, User.class);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Set role by request type
        if(registerRequest.getRole().equalsIgnoreCase("student")) {
            user.setRole(UserRoleEnum.STUDENT);
        } else if(registerRequest.getRole().equalsIgnoreCase("parent")) {
            user.setRole(UserRoleEnum.PARENT);
        } else if(registerRequest.getRole().equalsIgnoreCase("psychologist")) {
            user.setRole(UserRoleEnum.PSYCHOLOGIST);
        } else {
            return "Role not found";
        }
        authenticationRepository.save(user);
        return "Register successfully";
    }
}
