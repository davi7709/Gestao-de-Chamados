package com.davi.gestaodechamados.model;

import com.davi.gestaodechamados.enums.Prioridade;
import com.davi.gestaodechamados.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Chamados")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;
    private String descricao;
    private String solicitante;
    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataUltimaAtualizacao;

    public Chamado(){}

    public Chamado(String titulo, String descricao, String solicitante, Prioridade prioridade) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.prioridade = prioridade;
    }

    public Chamado(String titulo, String descricao, String solicitante, Prioridade prioridade, Status status, LocalDateTime dataAbertura, LocalDateTime dataUltimaAtualizacao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.solicitante = solicitante;
        this.prioridade = prioridade;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Chamado chamado = (Chamado) o;
        return Id == chamado.Id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Id);
    }
}
