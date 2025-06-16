// src/main/java/com/clinica/Clinica/controller/TestController.java
package com.clinica.Clinica.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * Rota pública para teste.
     * Não requer token.
     */
    @GetMapping("/public")
    public String publicAccess() {
        return "Conteúdo público, não requer token.";
    }

    /**
     * Rota protegida para usuários autenticados.
     * Retorna uma saudação contendo o nome do usuário extraído do Authentication.
     */
    @GetMapping("/user")
    public String userAccess(Authentication authentication) {
        String username = authentication.getName();
        return "Olá, " + username + "! Você está autenticado.";
    }

    /**
     * Rota protegida adicional (por exemplo, para administradores).
     * Atualmente, exige apenas que o usuário esteja autenticado.
     */
    @GetMapping("/admin")
    public String adminAccess(Authentication authentication) {
        String username = authentication.getName();
        return "Olá, " + username + "! Você está na área de admin.";
    }
}
