package com.davi.gestaodechamados.Dto;

import com.davi.gestaodechamados.enums.Prioridade;
import com.davi.gestaodechamados.enums.Status;
import com.davi.gestaodechamados.model.Chamado;

import java.time.LocalDateTime;

public record ChamadoResponse(
        Long id,
        String titulo,
        String descricao,
        String solicitante,
        Prioridade prioridade,
        Status status,
        LocalDateTime dataAbertura,
        LocalDateTime dataUltimaAtualizacao
) {

    // Factory method: converte a entidade JPA em DTO de resposta
    public static ChamadoResponse from(Chamado chamado) {
        return new ChamadoResponse(
                chamado.getId(),
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getSolicitante(),
                chamado.getPrioridade(),
                chamado.getStatus(),
                chamado.getDataAbertura(),
                chamado.getDataUltimaAtualizacao()
        );
    }
}
