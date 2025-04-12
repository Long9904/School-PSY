package com.main.project.repository;

import com.main.project.entities.ParentStudent;
import com.main.project.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentStudentRepository extends JpaRepository<ParentStudent, Long> {
    boolean existsByStudent(User student);    // kiểm tra student đã có liên kết chưa
    boolean existsByParent(User parent);      // kiểm tra parent đã có liên kết chưa
}
