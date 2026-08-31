package com.davi.gestaodechamados.Dto;

import com.davi.gestaodechamados.enums.Prioridade;
import com.davi.gestaodechamados.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChamadoRequest(

        @NotBlank(message = "O titulo e obrigatorio")
        String titulo,

        String descricao,

        @NotBlank(message = "O solicitante e obrigatorio")
        String solicitante,

        @NotNull(message = "A prioridade e obrigatoria")
        Prioridade prioridade

) {}
