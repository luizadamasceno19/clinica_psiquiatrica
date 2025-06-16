package com.clinica.Clinica.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.clinica.Clinica.model.Paciente;
import com.clinica.Clinica.model.Role;
import com.clinica.Clinica.model.User;
import com.clinica.Clinica.repository.PacienteRepository;
import com.clinica.Clinica.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UserService userService;

    /** 
     * Somente ADMIN pode cadastrar pacientes.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Paciente> createPaciente(@Valid @RequestBody Paciente paciente) {
        // Gera username a partir do nome
        String username = paciente.getNome()
                                  .trim()
                                  .toLowerCase()
                                  .replaceAll("\\s+", ".");
        paciente.setUsername(username);

        // Salva paciente
        Paciente saved = pacienteRepository.save(paciente);

        // Cria usuário de autenticação
        User user = new User();
        user.setUsername(username);
        user.setEmail(paciente.getEmail().trim());
        user.setPassword(paciente.getPassword());
        user.setRole(Role.ROLE_CLIENTE);
        userService.saveUser(user);

        return ResponseEntity.ok(saved);
    }

    /** Público: lista todos os pacientes. */
    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes() {
        return ResponseEntity.ok(pacienteRepository.findAll());
    }
}
