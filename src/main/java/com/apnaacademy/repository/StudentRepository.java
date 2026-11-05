package com.apnaacademy.repository;

import com.apnaacademy.entity.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
