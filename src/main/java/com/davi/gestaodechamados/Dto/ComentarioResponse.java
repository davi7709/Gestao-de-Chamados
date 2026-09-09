package com.davi.gestaodechamados.Dto;

import com.davi.gestaodechamados.model.Comentario;

import java.time.LocalDateTime;

public record ComentarioResponse(Long id, String comentario, LocalDateTime dataCriacao) {
    public static ComentarioResponse from(Comentario comentario) {
        return new ComentarioResponse(
                comentario.getId(),
                comentario.getTexto(),
                comentario.getDataCriacao()
        );
    }
}
