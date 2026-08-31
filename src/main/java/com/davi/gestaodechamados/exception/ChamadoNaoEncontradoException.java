package com.davi.gestaodechamados.exception;

public class ChamadoNaoEncontradoException extends RuntimeException {
    public ChamadoNaoEncontradoException(Long id) {
        super("Chamado com o id " + id + " Não encontrado");
    }
}
