package com.davi.gestaodechamados.repository;

import com.davi.gestaodechamados.enums.Status;
import com.davi.gestaodechamados.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    List<Chamado> findByStatus(Status status);
}
