package com.main.project.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentStudent {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent; // The parent linked to the student

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student; // The student linked to the parent

}
