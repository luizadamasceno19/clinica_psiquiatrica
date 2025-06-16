// src/main/java/com/clinica/Clinica/model/Psiquiatra.java
package com.clinica.Clinica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "PSIQUIATRAs")
public class Psiquiatra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especialidade;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String fone;

    // PASSWORD só é lido do JSON de requisição, não aparece no JSON de resposta
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    // Ignora consultas na saída JSON para evitar recursão
    @JsonIgnore
    @OneToMany(mappedBy = "Psiquiatra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consulta> consultas;

    public Psiquiatra() { }

    public Psiquiatra(Long id, String nome, String especialidade, String email, String fone, String password, List<Consulta> consultas) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.email = email;
        this.fone = fone;
        this.password = password;
        this.consultas = consultas;
    }

    // getters e setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFone() { return fone; }
    public void setFone(String fone) { this.fone = fone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public List<Consulta> getConsultas() { return consultas; }
    public void setConsultas(List<Consulta> consultas) { this.consultas = consultas; }
}
