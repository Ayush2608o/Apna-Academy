package com.apnaacademy.config;

import com.apnaacademy.entity.UserAccount;
import com.apnaacademy.repository.UserAccountRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UserAccountRepository users;
    private final PasswordEncoder passwords;
    private final String email;
    private final String password;

    public AdminBootstrap(UserAccountRepository users, PasswordEncoder passwords,
                          @Value("${admin.bootstrap.email:}") String email,
                          @Value("${admin.bootstrap.password:}") String password) {
        this.users = users;
        this.passwords = passwords;
        this.email = email.trim();
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isEmpty() && password.isEmpty()) {
            return;
        }
        if (email.isEmpty() || email.length() > 150 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || password.isEmpty() || password.length() < 12 || password.length() > 72
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "Set a valid ADMIN_BOOTSTRAP_EMAIL and ADMIN_BOOTSTRAP_PASSWORD (12-72 characters and at most 72 UTF-8 bytes), or leave both empty.");
        }
        if (users.existsByEmailIgnoreCase(email)) {
            return;
        }

        UserAccount account = new UserAccount();
        account.setName("Administrator");
        account.setEmail(email.toLowerCase(java.util.Locale.ROOT));
        account.setPassword(passwords.encode(password));
        account.setRole("ADMIN");
        account.setEnabled(true);
        users.save(account);
    }
}
