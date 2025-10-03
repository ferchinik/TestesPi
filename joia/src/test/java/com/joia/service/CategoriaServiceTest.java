package com.joia.service;

import com.joia.entity.Categoria;
import com.joia.entity.Joia; // Import necessário para o teste de delete
import com.joia.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @InjectMocks // Cria uma instância real de CategoriaService e injeta os mocks declarados abaixo
    private CategoriaService categoriaService;

    @Mock // Cria um mock (simulação) para CategoriaRepository
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaValida;
    private Categoria categoriaComJoias;

    @BeforeEach // Método executado antes de cada teste
    void setUp() {
        // Configuração inicial para os testes
        categoriaValida = new Categoria(1L, "Anéis", new ArrayList<>()); // Categoria sem joias associadas

        categoriaComJoias = new Categoria(2L, "Colares", new ArrayList<>());
        Joia joiaMock = new Joia(); // Cria uma joia mock simples
        joiaMock.setId(10L); // Define um ID para a joia mock
        joiaMock.setNome("Colar de Pérolas");
        // Inicializa a lista de joias se for nula
        if (categoriaComJoias.getJoias() == null) {
            categoriaComJoias.setJoias(new ArrayList<>());
        }
        categoriaComJoias.getJoias().add(joiaMock); // Associa a joia à categoriaComJoias
    }

    // --- Testes para o método save ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Salvar categoria com sucesso quando nome válido")
    void save_deveSalvarComSucesso_quandoNomeValido() {
        // Arrange (Organizar): Define o estado inicial e o comportamento esperado do mock.
        Categoria novaCategoria = new Categoria(null, "Pulseiras", null); // Categoria a ser salva
        // Simula que o repositório retornará a categoria com um ID após salvar.
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(new Categoria(3L, "Pulseiras", new ArrayList<>()));

        // Act (Agir): Executa o método que está sendo testado.
        String resultado = categoriaService.save(novaCategoria);

        // Assert (Verificar): Verifica se o resultado é o esperado e se os mocks foram chamados corretamente.
        assertThat(resultado).isEqualTo("Categoria salva com sucesso!");
        verify(categoriaRepository, times(1)).save(any(Categoria.class)); // Verifica se o método save do repositório foi chamado 1 vez.
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao salvar categoria com nome vazio")
    void save_deveLancarExcecao_quandoNomeVazio() {
        // Arrange
        Categoria categoriaInvalida = new Categoria(null, "", null);

        // Act & Assert
        // Verifica se uma IllegalArgumentException é lançada com a mensagem correta.
        assertThatThrownBy(() -> categoriaService.save(categoriaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome da categoria não pode ser vazio.");

        // Verifica que o método save do repositório NUNCA foi chamado.
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    // --- Testes para o método findById ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Encontrar categoria por ID existente")
    void findById_deveRetornarCategoria_quandoIdExiste() {
        // Arrange
        Long idExistente = categoriaValida.getId();
        // Simula que o repositório encontrará a categoria.
        when(categoriaRepository.findById(idExistente)).thenReturn(Optional.of(categoriaValida));

        // Act
        Categoria categoriaEncontrada = categoriaService.findById(idExistente);

        // Assert
        assertThat(categoriaEncontrada).isNotNull();
        assertThat(categoriaEncontrada.getId()).isEqualTo(idExistente);
        assertThat(categoriaEncontrada.getNome()).isEqualTo(categoriaValida.getNome());
        verify(categoriaRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao buscar categoria por ID inexistente")
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        // Simula que o repositório NÃO encontrará a categoria.
        when(categoriaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.findById(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria ID " + idInexistente + " não encontrada!");
        verify(categoriaRepository, times(1)).findById(idInexistente);
    }

    // --- Testes para o método findAll ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Retornar todas as categorias")
    void findAll_deveRetornarTodasCategorias() {
        // Arrange
        List<Categoria> listaCategorias = List.of(categoriaValida, categoriaComJoias);
        when(categoriaRepository.findAll()).thenReturn(listaCategorias);

        // Act
        List<Categoria> resultado = categoriaService.findAll();

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.size()).isEqualTo(2);
        assertThat(resultado).containsExactlyInAnyOrder(categoriaValida, categoriaComJoias);
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Retornar lista vazia quando não há categorias")
    void findAll_deveRetornarListaVazia_quandoNaoHaCategorias() {
        // Arrange
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Categoria> resultado = categoriaService.findAll();

        // Assert
        assertThat(resultado).isNotNull().isEmpty();
        verify(categoriaRepository, times(1)).findAll();
    }

    // --- Testes para o método update ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Atualizar categoria com sucesso")
    void update_deveAtualizarComSucesso_quandoDadosValidosEIdExistente() {
        // Arrange
        Long idParaAtualizar = categoriaValida.getId();
        Categoria categoriaComDadosNovos = new Categoria(idParaAtualizar, "Anéis Modificados", null);

        // Simula que a categoria existe
        when(categoriaRepository.findById(idParaAtualizar)).thenReturn(Optional.of(categoriaValida));
        // Simula a ação de salvar, retornando a categoria que foi passada para o save
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String resultado = categoriaService.update(categoriaComDadosNovos, idParaAtualizar);

        // Assert
        assertThat(resultado).isEqualTo("Categoria ID " + idParaAtualizar + " atualizada com sucesso!");
        verify(categoriaRepository).findById(idParaAtualizar); // Verifica se buscou a categoria
        // Verifica se salvou a categoria correta
        verify(categoriaRepository).save(argThat(cat ->
                cat.getId().equals(idParaAtualizar) &&
                        cat.getNome().equals("Anéis Modificados")
        ));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao atualizar categoria com nome vazio")
    void update_deveLancarExcecao_quandoNomeVazio() {
        // Arrange
        Long idParaAtualizar = categoriaValida.getId();
        Categoria categoriaInvalida = new Categoria(idParaAtualizar, "", null);
        // Não precisamos mockar findById neste caso, pois a validação do nome ocorre antes
        // Apenas para garantir que o service não prossiga se o nome for inválido ANTES de buscar:
        // when(categoriaRepository.findById(idParaAtualizar)).thenReturn(Optional.of(categoriaValida));


        // Act & Assert
        assertThatThrownBy(() -> categoriaService.update(categoriaInvalida, idParaAtualizar))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome da categoria não pode ser vazio.");

        verify(categoriaRepository, never()).findById(anyLong()); // Não deve chamar findById se a validação inicial falhar
        verify(categoriaRepository, never()).save(any(Categoria.class)); // Não deve chamar save
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao atualizar categoria com ID inexistente")
    void update_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        Categoria categoriaComDadosNovos = new Categoria(idInexistente, "Nome Qualquer", null);
        // Simula que ao tentar buscar a categoria para atualizar, ela não é encontrada.
        when(categoriaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.update(categoriaComDadosNovos, idInexistente))
                .isInstanceOf(RuntimeException.class) // A exceção vem do findById interno no update
                .hasMessage("Categoria ID " + idInexistente + " não encontrada!");

        verify(categoriaRepository).findById(idInexistente); // Verifica que tentou buscar
        verify(categoriaRepository, never()).save(any(Categoria.class)); // Mas não chegou a salvar
    }

    // --- Testes para o método delete ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deletar categoria com sucesso quando não há joias associadas")
    void delete_deveDeletarComSucesso_quandoNaoHaJoiasAssociadas() {
        // Arrange
        Long idParaDeletar = categoriaValida.getId(); // categoriaValida não tem joias no setUp
        when(categoriaRepository.findById(idParaDeletar)).thenReturn(Optional.of(categoriaValida));
        doNothing().when(categoriaRepository).deleteById(idParaDeletar); // Mock para o método deleteById

        // Act
        String resultado = categoriaService.delete(idParaDeletar);

        // Assert
        assertThat(resultado).isEqualTo("Categoria ID " + idParaDeletar + " deletada com sucesso!");
        verify(categoriaRepository).findById(idParaDeletar);
        verify(categoriaRepository).deleteById(idParaDeletar);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao deletar categoria com joias associadas")
    void delete_deveLancarExcecao_quandoHaJoiasAssociadas() {
        // Arrange
        Long idParaDeletar = categoriaComJoias.getId(); // categoriaComJoias TEM uma joia associada no setUp
        when(categoriaRepository.findById(idParaDeletar)).thenReturn(Optional.of(categoriaComJoias));

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.delete(idParaDeletar))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Não é possível excluir a categoria ID " + idParaDeletar + " pois ela está associada a 1 joia(s).");

        verify(categoriaRepository).findById(idParaDeletar);
        verify(categoriaRepository, never()).deleteById(anyLong()); // Não deve chamar deleteById
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao deletar categoria com ID inexistente")
    void delete_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(categoriaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoriaService.delete(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria ID " + idInexistente + " não encontrada!");

        verify(categoriaRepository).findById(idInexistente);
        verify(categoriaRepository, never()).deleteById(anyLong());
    }

    // --- Testes para findByNomeContaining ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar categorias contendo o termo no nome")
    void findByNomeContaining_deveRetornarCategoriasCorrespondentes() {
        // Arrange
        String termoBusca = "An"; // Deve encontrar "Anéis"
        List<Categoria> listaMock = List.of(categoriaValida);
        when(categoriaRepository.findByNomeContainingIgnoreCase(termoBusca)).thenReturn(listaMock);

        // Act
        List<Categoria> resultado = categoriaService.findByNomeContaining(termoBusca);

        // Assert
        assertThat(resultado).isNotNull().hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo(categoriaValida.getNome());
        verify(categoriaRepository).findByNomeContainingIgnoreCase(termoBusca);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia se nenhum nome de categoria contém o termo")
    void findByNomeContaining_deveRetornarListaVazia_quandoNenhumNomeCorresponde() {
        // Arrange
        String termoBusca = "Inexistente";
        when(categoriaRepository.findByNomeContainingIgnoreCase(termoBusca)).thenReturn(Collections.emptyList());

        // Act
        List<Categoria> resultado = categoriaService.findByNomeContaining(termoBusca);

        // Assert
        assertThat(resultado).isNotNull().isEmpty();
        verify(categoriaRepository).findByNomeContainingIgnoreCase(termoBusca);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia ao buscar por nome vazio ou nulo")
    void findByNomeContaining_deveRetornarListaVazia_quandoNomeBuscaVazioOuNulo() {
        // Teste com string vazia
        List<Categoria> resultadoVazio = categoriaService.findByNomeContaining("");
        assertThat(resultadoVazio).isNotNull().isEmpty();

        // Teste com null
        List<Categoria> resultadoNulo = categoriaService.findByNomeContaining(null);
        assertThat(resultadoNulo).isNotNull().isEmpty();

        // Verifica que o repositório não foi chamado em nenhum dos casos anteriores, pois a validação está no service
        verify(categoriaRepository, never()).findByNomeContainingIgnoreCase(anyString());
    }

    // --- Testes para findAllByOrderByNomeAsc ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar categorias ordenadas por nome ascendente")
    void findAllByOrderByNomeAsc_deveRetornarCategoriasOrdenadas() {
        // Arrange
        Categoria catA = new Categoria(1L, "Anéis", null);
        Categoria catB = new Categoria(2L, "Brincos", null);
        Categoria catC = new Categoria(3L, "Colares", null);
        List<Categoria> listaOrdenadaMock = List.of(catA, catB, catC); // Supondo que o repo retorna assim
        when(categoriaRepository.findAllByOrderByNomeAsc()).thenReturn(listaOrdenadaMock);

        // Act
        List<Categoria> resultado = categoriaService.findAllByOrderByNomeAsc();

        // Assert
        assertThat(resultado).isNotNull().hasSize(3);
        assertThat(resultado).containsExactly(catA, catB, catC); // Verifica a ordem
        verify(categoriaRepository).findAllByOrderByNomeAsc();
    }
}