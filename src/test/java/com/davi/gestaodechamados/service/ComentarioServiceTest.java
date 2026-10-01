package com.davi.gestaodechamados.service;

import com.davi.gestaodechamados.enums.Categoria;
import com.davi.gestaodechamados.exception.ChamadoNaoEncontradoException;
import com.davi.gestaodechamados.model.Chamado;
import com.davi.gestaodechamados.model.Comentario;
import com.davi.gestaodechamados.enums.Prioridade;
import com.davi.gestaodechamados.repository.ComentarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private ChamadoService chamadoService;

    @InjectMocks
    private ComentarioService comentarioService;

    @Test
    void deveAdicionarComentarioAoChamadoExistente() {
        Chamado chamado = new Chamado("Impressora não liga", "desc", "Davi", Categoria.REDE, Prioridade.ALTA);
        chamado.setId(1L);

        Comentario salvo = new Comentario();
        salvo.setId(10L);
        salvo.setComentario("Testando o primeiro comentário");
        salvo.setChamado(chamado);

        when(chamadoService.buscaPorId(1L)).thenReturn(chamado);
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(salvo);

        Comentario resultado = comentarioService.adicionarComentario(1L, "Testando o primeiro comentário");

        assertNotNull(resultado.getId());
        assertEquals("Testando o primeiro comentário", resultado.getComentario());
        verify(chamadoService, times(1)).buscaPorId(1L);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    @Test
    void deveLancarExcecaoAoComentarChamadoInexistente() {
        when(chamadoService.buscaPorId(999L)).thenThrow(new ChamadoNaoEncontradoException(999L));

        assertThrows(ChamadoNaoEncontradoException.class,
                () -> comentarioService.adicionarComentario(999L, "Algum texto"));

        // Garante que, tendo falhado ao buscar o chamado, o save NUNCA foi chamado
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    void deveListarComentariosDeUmChamadoExistente() {
        Chamado chamado = new Chamado("Título", "desc", "Davi",Categoria.REDE, Prioridade.MEDIA);
        chamado.setId(1L);

        Comentario c1 = new Comentario();
        c1.setComentario("Primeiro comentário");
        Comentario c2 = new Comentario();
        c2.setComentario("Segundo comentário");

        when(chamadoService.buscaPorId(1L)).thenReturn(chamado);
        when(comentarioRepository.findByChamadoId(1L)).thenReturn(List.of(c1, c2));

        List<Comentario> resultado = comentarioService.listarComentarios(1L);

        assertEquals(2, resultado.size());
    }

    @Test
    void deveLancarExcecaoAoListarComentariosDeChamadoInexistente() {
        when(chamadoService.buscaPorId(999L)).thenThrow(new ChamadoNaoEncontradoException(999L));

        assertThrows(ChamadoNaoEncontradoException.class,
                () -> comentarioService.listarComentarios(999L));

        verify(comentarioRepository, never()).findByChamadoId(any());
    }
}
