package com.joia.service;

import com.joia.entity.Fornecedor;
import com.joia.entity.Joia; // Import necessário para teste de delete
import com.joia.repository.FornecedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Static imports para facilitar a leitura das asserções e mocks
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FornecedorServiceTest {

    @InjectMocks
    private FornecedorService fornecedorService;

    @Mock
    private FornecedorRepository fornecedorRepository;

    private Fornecedor fornecedorValido;
    private Fornecedor fornecedorComJoias;
    private Fornecedor fornecedorSemJoias;

    @BeforeEach
    void setUp() {
        // Inicializa fornecedores de teste
        fornecedorValido = new Fornecedor(1L, "Ouro Nobre Ltda", new ArrayList<>());
        fornecedorComJoias = new Fornecedor(2L, "Prata Fina SA", new ArrayList<>());
        fornecedorSemJoias = new Fornecedor(3L, "Gemas Raras Com", new ArrayList<>());

        // Adiciona uma joia mockada ao fornecedorComJoias para teste de delete
        Joia joiaMock = new Joia();
        joiaMock.setId(10L);
        fornecedorComJoias.getJoias().add(joiaMock);
    }

    // --- Testes para save ---

    @Test
    @DisplayName("[Unit Test] Deve salvar fornecedor com sucesso")
    void save_deveSalvarComSucesso_quandoNomeValido() {
        // Arrange
        Fornecedor novoFornecedor = new Fornecedor(null, "Joias & Cia", null);
        Fornecedor fornecedorSalvo = new Fornecedor(4L, "Joias & Cia", new ArrayList<>());
        when(fornecedorRepository.save(any(Fornecedor.class))).thenReturn(fornecedorSalvo);

        // Act
        String resultado = fornecedorService.save(novoFornecedor);

        // Assert
        assertThat(resultado).isEqualTo("Fornecedor salvo com sucesso!");
        verify(fornecedorRepository, times(1)).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar fornecedor com nome vazio")
    void save_deveLancarExcecao_quandoNomeVazio() {
        // Arrange
        Fornecedor fornecedorInvalido = new Fornecedor(null, "", null);

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.save(fornecedorInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome do fornecedor não pode ser vazio.");
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    // --- Testes para findById ---

    @Test
    @DisplayName("[Unit Test] Deve retornar fornecedor quando ID existe")
    void findById_deveRetornarFornecedor_quandoIdExiste() {
        // Arrange
        Long idExistente = fornecedorValido.getId();
        when(fornecedorRepository.findById(idExistente)).thenReturn(Optional.of(fornecedorValido));

        // Act
        Fornecedor fornecedorEncontrado = fornecedorService.findById(idExistente);

        // Assert
        assertThat(fornecedorEncontrado).isNotNull();
        assertThat(fornecedorEncontrado.getId()).isEqualTo(idExistente);
        verify(fornecedorRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção quando ID não existe em findById")
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(fornecedorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.findById(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Fornecedor ID " + idInexistente + " não encontrado!");
        verify(fornecedorRepository, times(1)).findById(idInexistente);
    }

    // --- Testes para findAll ---

    @Test
    @DisplayName("[Unit Test] Deve retornar todos os fornecedores")
    void findAll_deveRetornarTodosFornecedores() {
        // Arrange
        List<Fornecedor> listaMock = List.of(fornecedorValido, fornecedorComJoias);
        when(fornecedorRepository.findAll()).thenReturn(listaMock);

        // Act
        List<Fornecedor> resultado = fornecedorService.findAll();

        // Assert
        assertThat(resultado).isNotNull().hasSize(2).containsExactly(fornecedorValido, fornecedorComJoias);
        verify(fornecedorRepository, times(1)).findAll();
    }

    // --- Testes para update ---

    @Test
    @DisplayName("[Unit Test] Deve atualizar fornecedor com sucesso")
    void update_deveAtualizarComSucesso() {
        // Arrange
        Long idParaAtualizar = fornecedorValido.getId();
        Fornecedor dadosAtualizacao = new Fornecedor(idParaAtualizar, "Ouro Nobre Atualizado", null);
        when(fornecedorRepository.findById(idParaAtualizar)).thenReturn(Optional.of(fornecedorValido));
        when(fornecedorRepository.save(any(Fornecedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String resultado = fornecedorService.update(dadosAtualizacao, idParaAtualizar);

        // Assert
        assertThat(resultado).isEqualTo("Fornecedor ID " + idParaAtualizar + " atualizado com sucesso!");
        verify(fornecedorRepository).findById(idParaAtualizar);
        verify(fornecedorRepository).save(argThat(f ->
                f.getId().equals(idParaAtualizar) &&
                        f.getNome().equals(dadosAtualizacao.getNome())
        ));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao atualizar fornecedor com nome vazio")
    void update_deveLancarExcecao_quandoNomeVazio() {
        // Arrange
        Long idParaAtualizar = fornecedorValido.getId();
        Fornecedor dadosInvalidos = new Fornecedor(idParaAtualizar, "", null);
        // Não precisa mockar findById aqui, a validação ocorre antes

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.update(dadosInvalidos, idParaAtualizar))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome do fornecedor não pode ser vazio.");
        verify(fornecedorRepository, never()).findById(anyLong());
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao atualizar fornecedor com ID inexistente")
    void update_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        Fornecedor dadosAtualizacao = new Fornecedor(idInexistente, "Nome Novo", null);
        when(fornecedorRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.update(dadosAtualizacao, idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Fornecedor ID " + idInexistente + " não encontrado!");
        verify(fornecedorRepository).findById(idInexistente);
        verify(fornecedorRepository, never()).save(any(Fornecedor.class));
    }

    // --- Testes para delete ---

    @Test
    @DisplayName("[Unit Test] Deve deletar fornecedor com sucesso quando não tem joias associadas")
    void delete_deveDeletarComSucesso_quandoNaoHaJoias() {
        // Arrange
        Long idParaDeletar = fornecedorSemJoias.getId();
        when(fornecedorRepository.findById(idParaDeletar)).thenReturn(Optional.of(fornecedorSemJoias));
        doNothing().when(fornecedorRepository).deleteById(idParaDeletar);

        // Act
        String resultado = fornecedorService.delete(idParaDeletar);

        // Assert
        assertThat(resultado).isEqualTo("Fornecedor ID " + idParaDeletar + " deletado com sucesso!");
        verify(fornecedorRepository).findById(idParaDeletar);
        verify(fornecedorRepository).deleteById(idParaDeletar);
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao deletar fornecedor com joias associadas")
    void delete_deveLancarExcecao_quandoHaJoiasAssociadas() {
        // Arrange
        Long idParaDeletar = fornecedorComJoias.getId(); // Este tem joia mockada no setUp
        when(fornecedorRepository.findById(idParaDeletar)).thenReturn(Optional.of(fornecedorComJoias));

        // Act & Assert
        assertThatThrownBy(() -> fornecedorService.delete(idParaDeletar))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é possível excluir o fornecedor ID " + idParaDeletar + " pois ele está associado a");
        verify(fornecedorRepository).findById(idParaDeletar);
        verify(fornecedorRepository, never()).deleteById(anyLong());
    }

    // --- Testes para findByNomeContaining ---

    @Test
    @DisplayName("[Unit Test] Deve retornar fornecedores contendo o termo no nome")
    void findByNomeContaining_deveRetornarFornecedoresCorrespondentes() {
        // Arrange
        String termo = "Nobre";
        List<Fornecedor> listaMock = List.of(fornecedorValido);
        when(fornecedorRepository.findByNomeContainingIgnoreCase(termo)).thenReturn(listaMock);

        // Act
        List<Fornecedor> resultado = fornecedorService.findByNomeContaining(termo);

        // Assert
        assertThat(resultado).isNotNull().hasSize(1).containsExactly(fornecedorValido);
        verify(fornecedorRepository).findByNomeContainingIgnoreCase(termo);
    }

    @Test
    @DisplayName("[Unit Test] Deve retornar lista vazia ao buscar por nome inexistente em findByNomeContaining")
    void findByNomeContaining_deveRetornarListaVazia_quandoNomeNaoEncontrado() {
        // Arrange
        String termo = "Inexistente";
        when(fornecedorRepository.findByNomeContainingIgnoreCase(termo)).thenReturn(Collections.emptyList());

        // Act
        List<Fornecedor> resultado = fornecedorService.findByNomeContaining(termo);

        // Assert
        assertThat(resultado).isNotNull().isEmpty();
        verify(fornecedorRepository).findByNomeContainingIgnoreCase(termo);
    }

    @Test
    @DisplayName("[Unit Test] Deve retornar lista vazia ao buscar por nome vazio ou nulo em findByNomeContaining")
    void findByNomeContaining_deveRetornarListaVazia_quandoNomeVazioOuNulo() {
        // Arrange & Act & Assert para nome vazio
        List<Fornecedor> resultadoVazio = fornecedorService.findByNomeContaining("");
        assertThat(resultadoVazio).isNotNull().isEmpty();

        // Arrange & Act & Assert para nome nulo
        List<Fornecedor> resultadoNulo = fornecedorService.findByNomeContaining(null);
        assertThat(resultadoNulo).isNotNull().isEmpty();

        // Verifica que o repositório não foi chamado
        verify(fornecedorRepository, never()).findByNomeContainingIgnoreCase(anyString());
    }

    // --- Testes para findByJoiasIsNotEmpty ---

    @Test
    @DisplayName("[Unit Test] Deve retornar fornecedores que possuem joias")
    void findByJoiasIsNotEmpty_deveRetornarFornecedoresComJoias() {
        // Arrange
        List<Fornecedor> listaMock = List.of(fornecedorComJoias);
        when(fornecedorRepository.findByJoiasIsNotEmpty()).thenReturn(listaMock);

        // Act
        List<Fornecedor> resultado = fornecedorService.findByJoiasIsNotEmpty();

        // Assert
        assertThat(resultado).isNotNull().hasSize(1).containsExactly(fornecedorComJoias);
        verify(fornecedorRepository).findByJoiasIsNotEmpty();
    }

    // --- Testes para findAllByOrderByNomeAsc ---

    @Test
    @DisplayName("[Unit Test] Deve retornar fornecedores ordenados por nome ascendente")
    void findAllByOrderByNomeAsc_deveRetornarFornecedoresOrdenadosAsc() {
        // Arrange
        // Ordem esperada: Gemas Raras Com (3), Ouro Nobre Ltda (1), Prata Fina SA (2)
        List<Fornecedor> listaMockOrdenada = List.of(fornecedorSemJoias, fornecedorValido, fornecedorComJoias);
        when(fornecedorRepository.findAllByOrderByNomeAsc()).thenReturn(listaMockOrdenada);

        // Act
        List<Fornecedor> resultado = fornecedorService.findAllByOrderByNomeAsc();

        // Assert
        assertThat(resultado).isNotNull().hasSize(3).containsExactly(fornecedorSemJoias, fornecedorValido, fornecedorComJoias);
        verify(fornecedorRepository).findAllByOrderByNomeAsc();
    }

    // --- Testes para findByJoiasIsEmpty ---

    @Test
    @DisplayName("[Unit Test] Deve retornar fornecedores que não possuem joias")
    void findByJoiasIsEmpty_deveRetornarFornecedoresSemJoias() {
        // Arrange
        List<Fornecedor> listaMock = List.of(fornecedorValido, fornecedorSemJoias); // Assumindo que o valido também não tem joias no setup
        when(fornecedorRepository.findByJoiasIsEmpty()).thenReturn(listaMock);

        // Act
        List<Fornecedor> resultado = fornecedorService.findByJoiasIsEmpty();

        // Assert
        assertThat(resultado).isNotNull().hasSize(2).containsExactly(fornecedorValido, fornecedorSemJoias);
        verify(fornecedorRepository).findByJoiasIsEmpty();
    }
}
