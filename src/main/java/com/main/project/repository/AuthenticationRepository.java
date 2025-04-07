package com.main.project.repository;

import com.main.project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findUserById(Long id);
}
