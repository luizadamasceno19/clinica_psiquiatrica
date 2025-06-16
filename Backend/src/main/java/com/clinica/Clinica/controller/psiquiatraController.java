package com.clinica.Clinica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.Clinica.model.Psiquiatra;
import com.clinica.Clinica.model.Role;
import com.clinica.Clinica.model.User;
import com.clinica.Clinica.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/PSIQUIATRAs")
@CrossOrigin(origins = "*")
public class psiquiatraController {

    @Autowired
    private PsiquiatraRepository psiquiatraRepository;

    @Autowired
    private UserService userService;

    /** Somente ADMIN pode cadastrar Psiquiatra. */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Psiquiatra> createPSIQUIATRA(
            @Valid @RequestBody Psiquiatra dto
    ) {
        // 1) salva Psiquiatra na tabela PSIQUIATRAs
        Psiquiatra saved = psiquiatraRepository.save(dto);

        // 2) cria também usuário no sistema usando o nome como username
        User user = new User();
        // gera: "maria.silva" a partir de "Maria Silva"
        String username = dto.getNome()
                             .trim()
                             .toLowerCase()
                             .replaceAll("\\s+", ".");
        user.setUsername(username);
        user.setEmail(dto.getEmail().trim());
        user.setPassword(dto.getPassword());
        user.setRole(Role.ROLE_PSIQUIATRA);
        userService.saveUser(user);

        return ResponseEntity.ok(saved);
    }

    /** Público: lista todos. */
    @GetMapping
    public ResponseEntity<List<Psiquiatra>> getAllPSIQUIATRAs() {
        return ResponseEntity.ok(psiquiatraRepository.findAll());
    }

    /** Público: filtra por especialidade. */
    @GetMapping("/especialidade")
    public ResponseEntity<List<Psiquiatra>> getByEspecialidade(
            @RequestParam("especialidade") String especialidade
    ) {
        return ResponseEntity.ok(
        		psiquiatraRepository.findByEspecialidade(especialidade)
        );
    }
}
