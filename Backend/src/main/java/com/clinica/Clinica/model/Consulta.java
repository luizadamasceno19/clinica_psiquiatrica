// src/main/java/com/clinica/Clinica/model/Consulta.java
package com.clinica.Clinica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    // Agora relaciona com Psiquiatra em vez de Medico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PSIQUIATRA_id", nullable = false)
    private Psiquiatra Psiquiatra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    public Consulta() { }

    public Consulta(LocalDateTime data, Psiquiatra Psiquiatra, Paciente paciente) {
        this.data = data;
        this.Psiquiatra = Psiquiatra;
        this.paciente = paciente;
    }

    // ---------- Getters e setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Psiquiatra getPSIQUIATRA() {
        return Psiquiatra;
    }

    public void setPSIQUIATRA(Psiquiatra Psiquiatra) {
        this.Psiquiatra = Psiquiatra;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
