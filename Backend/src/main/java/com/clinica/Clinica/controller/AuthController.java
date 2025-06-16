// src/main/java/com/clinica/Clinica/controller/AuthController.java
package com.clinica.Clinica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.Clinica.model.Role;
import com.clinica.Clinica.model.User;
import com.clinica.Clinica.payload.request.LoginRequest;
import com.clinica.Clinica.payload.request.SignupRequest;
import com.clinica.Clinica.payload.response.JwtResponse;
import com.clinica.Clinica.repository.UserRepository;
import com.clinica.Clinica.security.JwtUtils;
import com.clinica.Clinica.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    // -----------------------------
    // Apenas ADMIN pode registrar
    // -----------------------------
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Username já em uso!");
        }
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Erro: Email já em uso!");
        }

        Role role;
        try {
            role = Role.valueOf("ROLE_" + signUpRequest.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Erro: Role inválida! Use 'admin', 'Psiquiatra' ou 'cliente'.");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername().trim());
        user.setEmail(signUpRequest.getEmail().trim());
        user.setPassword(signUpRequest.getPassword());
        user.setRole(role);
        userService.saveUser(user);

        return ResponseEntity.ok("Usuário registrado com sucesso com role = " + role.name());
    }

    // aberto para todos
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String jwt = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name());
        JwtResponse response = new JwtResponse(jwt, user.getId(), user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(response);
    }
}
