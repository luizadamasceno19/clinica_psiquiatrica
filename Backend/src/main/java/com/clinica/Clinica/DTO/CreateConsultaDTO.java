package com.clinica.Clinica.DTO;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CreateConsultaDTO {

    @NotNull(message = "PSIQUIATRAId é obrigatório")
    private Long PSIQUIATRAId;

    @NotNull(message = "pacienteId é obrigatório")
    private Long pacienteId;

    /** Formato ISO com segundos */
    @NotNull(message = "data é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime data;

    public CreateConsultaDTO() {}

    public Long getPSIQUIATRAId() { return PSIQUIATRAId; }
    public void setPSIQUIATRAId(Long PSIQUIATRAId) { this.PSIQUIATRAId = PSIQUIATRAId; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
}
