package com.davi.gestaodechamados.Dto;

import com.davi.gestaodechamados.enums.Status;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "O novo status e obrigatorio")
        Status status
) {}
