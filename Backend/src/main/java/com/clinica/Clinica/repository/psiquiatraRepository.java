// src/main/java/com/clinica/Clinica/repository/PSIQUIATRARepository.java
package com.clinica.Clinica.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.clinica.Clinica.model.Psiquiatra;

public interface PSIQUIATRARepository extends JpaRepository<Psiquiatra, Long> {
    List<Psiquiatra> findByEspecialidade(String especialidade);
}
