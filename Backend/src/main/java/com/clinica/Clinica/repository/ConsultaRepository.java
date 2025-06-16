package com.clinica.Clinica.repository;

import com.clinica.Clinica.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Busca todas as consultas com Psiquiatra e paciente carregados
    @Query("SELECT c FROM Consulta c JOIN FETCH c.Psiquiatra e JOIN FETCH c.paciente p")
    List<Consulta> findAllWithPSIQUIATRAPaciente();

    // Relatório: filtra por especialidade e/ou Psiquiatra
    @Query("""
        SELECT c 
        FROM Consulta c 
        JOIN FETCH c.Psiquiatra e 
        JOIN FETCH c.paciente p 
        WHERE (:PSIQUIATRAId IS NULL OR e.id = :PSIQUIATRAId) 
          AND (:especialidade IS NULL OR e.especialidade = :especialidade)
        """)
    List<Consulta> findByPSIQUIATRAOrEspecialidade(
        @Param("PSIQUIATRAId") Long PSIQUIATRAId,
        @Param("especialidade") String especialidade
    );

    // Busca todas as consultas de um paciente específico
    List<Consulta> findByPacienteId(Long pacienteId);
}
