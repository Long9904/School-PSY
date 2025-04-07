package com.main.project.utils;

import com.main.project.entities.User;
import com.main.project.repository.AuthenticationRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils implements ApplicationContextAware {
    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        authenticationRepository = applicationContext.getBean(AuthenticationRepository.class);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return authenticationRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
