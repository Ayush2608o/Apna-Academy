package com.apnaacademy.service;

import com.apnaacademy.dto.RegistrationForm;
import com.apnaacademy.entity.Student;
import com.apnaacademy.entity.UserAccount;
import com.apnaacademy.repository.StudentRepository;
import com.apnaacademy.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final UserAccountRepository users;
    private final StudentRepository students;
    private final PasswordEncoder passwords;

    public RegistrationService(UserAccountRepository users, StudentRepository students, PasswordEncoder passwords) {
        this.users = users;
        this.students = students;
        this.passwords = passwords;
    }

    @Transactional
    public void register(RegistrationForm form) {
        if (users.existsByEmailIgnoreCase(form.getEmail()) || students.existsByEmailIgnoreCase(form.getEmail())) {
            throw new IllegalArgumentException("An account already exists for this email.");
        }
        UserAccount account = new UserAccount();
        account.setName(form.getName().trim());
        account.setEmail(form.getEmail().trim().toLowerCase());
        account.setPassword(passwords.encode(form.getPassword()));
        account.setRole("STUDENT");
        account.setEnabled(true);
        account = users.save(account);

        Student student = new Student();
        student.setUserId(account.getId());
        student.setCourseId(form.getCourseId());
        student.setName(form.getName().trim());
        student.setEmail(form.getEmail().trim().toLowerCase());
        student.setPhone(form.getPhone());
        students.save(student);
    }
}
