package com.davi.gestaodechamados.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="Comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1000)
    private String texto;
    private LocalDateTime dataCriacao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chamado_id")
    private Chamado chamado;

    public Comentario() {}

    public Comentario(Long id, String texto, LocalDateTime dataCriacao,  Chamado chamado) {
        this.id = id;
        this.texto = texto;
        this.dataCriacao = dataCriacao;
        this.chamado = chamado;
    }

    public Comentario(String texto){
        this.texto = texto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Chamado getChamado() {
        return chamado;
    }

    public void setChamado(Chamado chamado) {
        this.chamado = chamado;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comentario that = (Comentario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @PrePersist
    protected void aoCriar() {
        this.dataCriacao = LocalDateTime.now();
    }
}
