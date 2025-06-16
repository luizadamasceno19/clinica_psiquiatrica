package com.clinica.Clinica.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinica.Clinica.DTO.CreateConsultaDTO;
import com.clinica.Clinica.model.Consulta;
import com.clinica.Clinica.model.Psiquiatra;
import com.clinica.Clinica.model.Paciente;
import com.clinica.Clinica.repository.ConsultaRepository;
import com.clinica.Clinica.repository.PSIQUIATRARepository;
import com.clinica.Clinica.repository.PacienteRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin(origins = "*")
public class ConsultaController {

    @Autowired private ConsultaRepository consultaRepository;
    @Autowired private PSIQUIATRARepository PSIQUIATRARepository;
    @Autowired private PacienteRepository pacienteRepository;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Agendamento - qualquer usuário autenticado */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createConsulta(
        @Valid @RequestBody CreateConsultaDTO dto,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            Map<String,String> erros = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fe -> 
                erros.put(fe.getField(), fe.getDefaultMessage()));
            return ResponseEntity.badRequest().body(erros);
        }

        Optional<Psiquiatra> eOpt = PSIQUIATRARepository.findById(dto.getPSIQUIATRAId());
        Optional<Paciente> pOpt = pacienteRepository.findById(dto.getPacienteId());
        if (eOpt.isEmpty() || pOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("erro","Psiquiatra ou Paciente não encontrado"));
        }

        Consulta saved = consultaRepository
            .save(new Consulta(dto.getData(), eOpt.get(), pOpt.get()));
        return ResponseEntity.ok(saved);
    }

    /** Todas as consultas - só ADMIN */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Consulta>> getAllConsultas() {
        return ResponseEntity.ok(consultaRepository.findAllWithPSIQUIATRAPaciente());
    }

    /** Meus agendamentos - qualquer usuário autenticado */
    @GetMapping("/meus")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMinhasConsultas(Principal principal) {
        String username = principal.getName();
        Optional<Paciente> optPaciente = pacienteRepository.findByUsername(username);
        if (optPaciente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("erro", 
                                     "Paciente não encontrado para o usuário: " + username));
        }

        List<Consulta> consultas = consultaRepository
            .findByPacienteId(optPaciente.get().getId());
        var resultado = new ArrayList<Map<String,Object>>();
        for (Consulta c : consultas) {
            resultado.add(Map.of(
                "data",          c.getData().format(DTF),
                "Psiquiatra",   c.getPSIQUIATRA().getNome(),
                "especialidade", c.getPSIQUIATRA().getEspecialidade()
            ));
        }
        return ResponseEntity.ok(resultado);
    }

    /** Relatório JSON - aberto */
    @GetMapping("/relatorio")
    public ResponseEntity<List<Map<String,Object>>> getRelatorio(
        @RequestParam(required = false) Long PSIQUIATRAId,
        @RequestParam(required = false) String especialidade
    ) {
        List<Consulta> consultas = consultaRepository
            .findByPSIQUIATRAOrEspecialidade(PSIQUIATRAId, especialidade);
        var resultado = new ArrayList<Map<String,Object>>();
        for (Consulta c : consultas) {
            resultado.add(Map.of(
                "data",              c.getData().format(DTF),
                "PSIQUIATRA_nome",  c.getPSIQUIATRA().getNome(),
                "especialidade",     c.getPSIQUIATRA().getEspecialidade(),
                "paciente_nome",     c.getPaciente().getNome()
            ));
        }
        return ResponseEntity.ok(resultado);
    }

    /** Relatório PDF - só ADMIN */
    @GetMapping("/relatorio/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> gerarRelatorioPdf(
        @RequestParam(required = false) Long PSIQUIATRAId,
        @RequestParam(required = false) String especialidade
    ) {
        try {
            List<Consulta> consultas = consultaRepository
                .findByPSIQUIATRAOrEspecialidade(PSIQUIATRAId, especialidade);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document doc = new Document(pdfDoc);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            doc.add(new Paragraph("Relatório de Consultas")
                        .setFont(font).setFontSize(16).setBold()
                        .setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("\n"));

            float[] cols = {100F,120F,120F,120F};
            Table table = new Table(cols).setWidth(100);
            table.addHeaderCell(new Cell().add(new Paragraph("Data/Hora").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Psiquiatra").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Especialidade").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Paciente").setBold()));

            for (Consulta c : consultas) {
                table.addCell(new Cell().add(new Paragraph(c.getData().format(DTF))));
                table.addCell(new Cell().add(new Paragraph(c.getPSIQUIATRA().getNome())));
                table.addCell(new Cell().add(new Paragraph(c.getPSIQUIATRA().getEspecialidade())));
                table.addCell(new Cell().add(new Paragraph(c.getPaciente().getNome())));
            }

            doc.add(table);
            doc.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment","relatorio_consultas.pdf");
            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

