package com.davi.gestaodechamados.enums;

public enum Prioridade {
    BAIXA(48),
    MEDIA(24),
    ALTA(8),
    CRITICA(4);

    private final Long limiteHoras;

    Prioridade(long limiteHoras) {
        this.limiteHoras = limiteHoras;
    }
    public Long getLimiteHoras() {
        return limiteHoras;
    }
}
