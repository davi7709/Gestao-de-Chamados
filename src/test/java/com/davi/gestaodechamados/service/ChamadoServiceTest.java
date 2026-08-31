package com.davi.gestaodechamados.service;

import com.davi.gestaodechamados.enums.Prioridade;
import com.davi.gestaodechamados.enums.Status;
import com.davi.gestaodechamados.exception.ChamadoNaoEncontradoException;
import com.davi.gestaodechamados.model.Chamado;

import com.davi.gestaodechamados.repository.ChamadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository repository;

    @InjectMocks
    private ChamadoService service;

    // ---------- criar ----------

    @Test
    void deveCriarChamadoERetornarComIdGerado() {
        Chamado novo = new Chamado("Impressora não liga", "desc", "Davi", Prioridade.ALTA);
        Chamado salvo = new Chamado("Impressora não liga", "desc", "Davi", Prioridade.ALTA);
        salvo.setId(1L); // simula o que o banco faria

        when(repository.save(novo)).thenReturn(salvo);

        Chamado resultado = service.criarChamado(novo);

        assertNotNull(resultado.getId());
        assertEquals("Impressora não liga", resultado.getTitulo());
        verify(repository, times(1)).save(novo);
    }

    // ---------- buscarPorId ----------

    @Test
    void deveRetornarChamadoQuandoIdExiste() {
        Chamado chamado = new Chamado("Título", "desc", "Davi", Prioridade.MEDIA);
        when(repository.findById(1L)).thenReturn(Optional.of(chamado));

        Chamado resultado = service.buscaPorId(1L);

        assertNotNull(resultado);
        assertEquals("Título", resultado.getTitulo());
    }

    @Test
    void deveLancarExcecaoQuandoIdNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ChamadoNaoEncontradoException.class, () -> service.buscaPorId(999L));
    }

    // ---------- listarTodos ----------

    @Test
    void deveListarTodosOsChamados() {
        Chamado c1 = new Chamado("A", "desc", "Davi", Prioridade.BAIXA);
        Chamado c2 = new Chamado("B", "desc", "Ana", Prioridade.ALTA);
        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<Chamado> resultado = service.todosChamados();

        assertEquals(2, resultado.size());
    }

    // ---------- buscarPorStatus ----------

    @Test
    void deveFiltrarChamadosPorStatus() {
        Chamado chamado = new Chamado("A", "desc", "Davi", Prioridade.MEDIA);
        chamado.setStatus(Status.EM_ANDAMENTO);
        when(repository.findByStatus(Status.EM_ANDAMENTO)).thenReturn(List.of(chamado));

        List<Chamado> resultado = service.buscaPorStatus(Status.EM_ANDAMENTO);

        assertEquals(1, resultado.size());
        assertEquals(Status.EM_ANDAMENTO, resultado.get(0).getStatus());
    }

    // ---------- editarChamado ----------

    @Test
    void deveEditarDadosGeraisDoChamado() {
        Chamado existente = new Chamado("Título antigo", "desc antiga", "Davi", Prioridade.BAIXA);
        existente.setId(1L);

        Chamado dadosNovos = new Chamado("Título novo", "desc nova", "Davi", Prioridade.ALTA);

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Chamado.class))).thenReturn(existente);

        Chamado resultado = service.editarChamado(1L, dadosNovos);

        assertEquals("Título novo", resultado.getTitulo());
        assertEquals(Prioridade.ALTA, resultado.getPrioridade());
    }

    @Test
    void deveLancarExcecaoAoEditarChamadoInexistente() {
        Chamado dadosNovos = new Chamado("Título", "desc", "Davi", Prioridade.ALTA);
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ChamadoNaoEncontradoException.class,
                () -> service.editarChamado(999L, dadosNovos));
    }

    // ---------- alterarStatus ----------

    @Test
    void deveAlterarStatusEAtualizarData() {
        Chamado chamado = new Chamado("Título", "desc", "Davi", Prioridade.MEDIA);
        LocalDateTime dataAntiga = LocalDateTime.now().minusDays(1);
        chamado.setDataUltimaAtualizacao(dataAntiga);

        when(repository.findById(1L)).thenReturn(Optional.of(chamado));
        when(repository.save(any(Chamado.class))).thenReturn(chamado);

        Chamado resultado = service.alteraChamado(1L, Status.EM_ANDAMENTO);

        assertEquals(Status.EM_ANDAMENTO, resultado.getStatus());
        assertTrue(resultado.getDataUltimaAtualizacao().isAfter(dataAntiga));
    }

    // ---------- buscarAtrasados ----------

    @Test
    void chamadoCriticoParadoDeveEstarAtrasado() {
        Chamado chamado = new Chamado("Servidor caiu", "desc", "Davi", Prioridade.CRITICA);
        chamado.setDataUltimaAtualizacao(LocalDateTime.now().minusHours(5));
        chamado.setStatus(Status.EM_ANDAMENTO);

        when(repository.findByStatusNotIn(anyList())).thenReturn(List.of(chamado));

        List<Chamado> atrasados = service.buscarAtrasados();

        assertEquals(1, atrasados.size());
    }

    @Test
    void chamadoBaixaPrioridadeParadoPoucoTempoNaoDeveEstarAtrasado() {
        Chamado chamado = new Chamado("Pedido de acesso", "desc", "Davi", Prioridade.BAIXA);
        chamado.setDataUltimaAtualizacao(LocalDateTime.now().minusHours(5));
        chamado.setStatus(Status.NOVO);

        when(repository.findByStatusNotIn(anyList())).thenReturn(List.of(chamado));

        List<Chamado> atrasados = service.buscarAtrasados();

        assertTrue(atrasados.isEmpty());
    }
}
