package com.davi.gestaodechamados.controller;

import com.davi.gestaodechamados.Dto.ChamadoRequest;
import com.davi.gestaodechamados.Dto.ChamadoResponse;
import com.davi.gestaodechamados.Dto.StatusUpdateRequest;
import com.davi.gestaodechamados.enums.Status;
import com.davi.gestaodechamados.model.Chamado;
import com.davi.gestaodechamados.service.ChamadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    private final ChamadoService service;

    public ChamadoController(ChamadoService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody ChamadoRequest request) {
        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                request.solicitante(),
                request.prioridade()
        );
        Chamado salvo = service.criarChamado(chamado);
        return ResponseEntity.status(HttpStatus.CREATED).body(ChamadoResponse.from(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ChamadoResponse>> listar(
            @RequestParam(required = false) Status status) {

        List<Chamado> chamados = (status != null)
                ? service.buscaPorStatus(status)
                : service.todosChamados();

        List<ChamadoResponse> resposta = chamados.stream()
                .map(ChamadoResponse::from)
                .toList();

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<ChamadoResponse>> listarAtrasados() {
        List<ChamadoResponse> resposta = service.buscarAtrasados().stream()
                .map(ChamadoResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponse> buscarPorId(@PathVariable Long id) {
        Chamado chamado = service.buscaPorId(id);
        return ResponseEntity.ok(ChamadoResponse.from(chamado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChamadoResponse> editar(
            @PathVariable Long id,
            @Valid @RequestBody ChamadoRequest request) {

        Chamado dadosAtualizados = new Chamado(
                request.titulo(),
                request.descricao(),
                request.solicitante(),
                request.prioridade()
        );
        Chamado atualizado = service.editarChamado(id, dadosAtualizados);
        return ResponseEntity.ok(ChamadoResponse.from(atualizado));
    }

    // PATCH /api/chamados/5/status -> alterar só o status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ChamadoResponse> alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        Chamado atualizado = service.alteraChamado(id, request.status());
        return ResponseEntity.ok(ChamadoResponse.from(atualizado));
    }
}
