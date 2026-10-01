package com.davi.gestaodechamados.service;

import com.davi.gestaodechamados.model.Chamado;
import com.davi.gestaodechamados.model.Comentario;
import com.davi.gestaodechamados.repository.ComentarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ChamadoService chamadoService;

    public ComentarioService(ComentarioRepository repository, ChamadoService chamadoService) {
        this.comentarioRepository = repository;
        this.chamadoService = chamadoService;
    }

    public List<Comentario> listarComentarios(Long chamadoId) {
        chamadoService.buscaPorId(chamadoId);
        return comentarioRepository.findByChamadoId(chamadoId);
    }

    public Comentario adicionarComentario(Long chamadoId, String comentario) {
        Chamado chamado = chamadoService.buscaPorId(chamadoId);

        Comentario coment = new Comentario();
        coment.setComentario(comentario);
        coment.setChamado(chamado);

        return comentarioRepository.save(coment);
    }
}
