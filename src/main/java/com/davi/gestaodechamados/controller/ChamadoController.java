package com.davi.gestaodechamados.controller;

import com.davi.gestaodechamados.Dto.*;
import com.davi.gestaodechamados.enums.Status;
import com.davi.gestaodechamados.model.Chamado;
import com.davi.gestaodechamados.model.Comentario;
import com.davi.gestaodechamados.service.ChamadoService;
import com.davi.gestaodechamados.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    private final ChamadoService chamadoService;
    private final ComentarioService comentarioService;

    public ChamadoController(ChamadoService service, ComentarioService comentarioService) {
        this.chamadoService = service;
        this.comentarioService = comentarioService;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody ChamadoRequest request) {
        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                request.solicitante(),
                request.prioridade()
        );
        Chamado salvo = chamadoService.criarChamado(chamado);
        return ResponseEntity.status(HttpStatus.CREATED).body(ChamadoResponse.from(salvo));
    }

    @GetMapping
    public ResponseEntity<Page<ChamadoResponse>> listar(
            @RequestParam(required = false) Status status, Pageable pageable) {

        Page<Chamado> pagina = (status != null)
                ? chamadoService.buscaPorStatus(status, pageable)
                : chamadoService.todosChamados(pageable);

        Page<ChamadoResponse> resposta = pagina
                .map(ChamadoResponse::from);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<ChamadoResponse>> listarAtrasados() {
        List<ChamadoResponse> resposta = chamadoService.buscarAtrasados().stream()
                .map(ChamadoResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoResponse> buscarPorId(@PathVariable Long id) {
        Chamado chamado = chamadoService.buscaPorId(id);
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
        Chamado atualizado = chamadoService.editarChamado(id, dadosAtualizados);
        return ResponseEntity.ok(ChamadoResponse.from(atualizado));
    }

    //alterar só o status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ChamadoResponse> alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {

        Chamado atualizado = chamadoService.alteraChamado(id, request.status());
        return ResponseEntity.ok(ChamadoResponse.from(atualizado));
    }

    //Adiciona Comentario
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioResponse> adicionarComentario(@PathVariable Long id, @Valid @RequestBody ComentarioRequest request) {
        Comentario criado = new Comentario(
                request.texto()
        );
        Comentario salvo = comentarioService.adicionarComentario(id, request.texto());
        return ResponseEntity.status(HttpStatus.CREATED).body(ComentarioResponse.from(criado));
    }

    //Lista Comentario
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<List<ComentarioResponse>> listarComentarios(@PathVariable Long id) {
        List<ComentarioResponse> resposta = comentarioService.listarComentarios(id).stream()
                .map(ComentarioResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

}
