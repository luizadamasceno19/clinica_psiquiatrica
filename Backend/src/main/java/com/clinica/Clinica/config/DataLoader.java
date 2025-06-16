// src/main/java/com/clinica/Clinica/config/DataLoader.java
package com.clinica.Clinica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.clinica.Clinica.model.Role;
import com.clinica.Clinica.model.User;
import com.clinica.Clinica.service.UserService;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@clinica.com");
            admin.setPassword("SenhaAdmin!23");
            admin.setRole(Role.ROLE_ADMIN);
            userService.saveUser(admin);
            System.out.println("Admin inicial criado: admin / SenhaAdmin!23");
        }
    }
}
