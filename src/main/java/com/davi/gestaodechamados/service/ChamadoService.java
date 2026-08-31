package com.davi.gestaodechamados.service;

import com.davi.gestaodechamados.enums.Status;
import com.davi.gestaodechamados.exception.ChamadoNaoEncontradoException;
import com.davi.gestaodechamados.model.Chamado;
import com.davi.gestaodechamados.repository.ChamadoRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;

    public ChamadoService(ChamadoRepository repository) {
        this.repository = repository;
    }

    public List<Chamado> todosChamados(){
        return repository.findAll();
    }

    public Chamado buscaPorId(Long id){
        return repository.findById(id).orElseThrow(() -> new ChamadoNaoEncontradoException(id));
    }

    public List<Chamado> buscaPorStatus(Status status){
        return repository.findByStatus(status);
    }

    public Chamado criarChamado(Chamado chamado){
        return repository.save(chamado);
    }

    public Chamado editarChamado(Long id, Chamado dadosAtualizados){
        Chamado existente = buscaPorId(id);

        existente.setTitulo(dadosAtualizados.getTitulo());
        existente.setDescricao(dadosAtualizados.getDescricao());
        existente.setSolicitante(dadosAtualizados.getSolicitante());
        existente.setPrioridade(dadosAtualizados.getPrioridade());
        existente.setDataUltimaAtualizacao(LocalDateTime.now());

        return repository.save(existente);
    }

    public Chamado alteraChamado(Long id, Status novoStatus){
        Chamado existente = buscaPorId(id);

        existente.setStatus(novoStatus);
        existente.setDataUltimaAtualizacao(LocalDateTime.now());

        return repository.save(existente);
    }

    public List<Chamado> buscarAtrasados(){
    List<Status> statusFinalizados = List.of(Status.RESOLVIDO, Status.ENCERRADO);
    List<Chamado> ativos = repository.findByStatusNotIn(statusFinalizados);

    return ativos.stream()
            .filter(this::estaAtrasado)
            .toList();
    }

    private boolean estaAtrasado(Chamado chamado) {
        long horasParado = Duration.between(chamado.getDataUltimaAtualizacao(), LocalDateTime.now()).toHours();
        return horasParado > chamado.getPrioridade().getLimiteHoras();
    }
}
