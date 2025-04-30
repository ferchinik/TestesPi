package com.joia.service;

import com.joia.entity.Categoria;
import com.joia.entity.Fornecedor;
import com.joia.entity.Joia;
import com.joia.entity.Pedido; // Import para teste de delete
import com.joia.repository.JoiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Static imports para facilitar a leitura das asserções e mocks
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq; // Para verificar parâmetros específicos
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoiaServiceTest {

    @InjectMocks
    private JoiaService joiaService;

    @Mock
    private JoiaRepository joiaRepository;

    private Joia joiaValida;
    private Joia joiaComPedidos;
    private Categoria categoriaValida;
    private Fornecedor fornecedorValido;

    @BeforeEach
    void setUp() {
        categoriaValida = new Categoria(1L, "Anéis", new ArrayList<>());
        fornecedorValido = new Fornecedor(1L, "Ouro Nobre", new ArrayList<>());

        joiaValida = new Joia(1L, "Anel Solitário", new BigDecimal("1500.00"), categoriaValida, fornecedorValido, new ArrayList<>());
        joiaComPedidos = new Joia(2L, "Colar Pérolas", new BigDecimal("850.50"), categoriaValida, fornecedorValido, new ArrayList<>());

        // Adiciona um pedido mockado à joiaComPedidos
        Pedido pedidoMock = new Pedido();
        joiaComPedidos.getPedidos().add(pedidoMock);
    }

    // --- Testes para save ---

    @Test
    @DisplayName("[Unit Test] Deve salvar joia com sucesso")
    void save_deveSalvarComSucesso_quandoDadosValidos() {
        // Arrange
        Joia novaJoia = new Joia(null, "Brinco Ouro", new BigDecimal("500.00"), categoriaValida, fornecedorValido, null);
        Joia joiaSalva = new Joia(3L, "Brinco Ouro", new BigDecimal("500.00"), categoriaValida, fornecedorValido, new ArrayList<>());
        when(joiaRepository.save(any(Joia.class))).thenReturn(joiaSalva);

        // Act
        String resultado = joiaService.save(novaJoia);

        // Assert
        assertThat(resultado).isEqualTo("Joia salva com sucesso!");
        verify(joiaRepository, times(1)).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com nome vazio")
    void save_deveLancarExcecao_quandoNomeVazio() {
        // Arrange
        Joia joiaInvalida = new Joia(null, "", new BigDecimal("500.00"), categoriaValida, fornecedorValido, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome da joia não pode ser vazio!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com preço nulo")
    void save_deveLancarExcecao_quandoPrecoNulo() {
        // Arrange
        Joia joiaInvalida = new Joia(null, "Nome Joia", null, categoriaValida, fornecedorValido, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O preço da joia não pode ser nulo ou negativo!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com preço negativo")
    void save_deveLancarExcecao_quandoPrecoNegativo() {
        // Arrange
        Joia joiaInvalida = new Joia(null, "Nome Joia", new BigDecimal("-10.00"), categoriaValida, fornecedorValido, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O preço da joia não pode ser nulo ou negativo!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com categoria nula")
    void save_deveLancarExcecao_quandoCategoriaNula() {
        // Arrange
        Joia joiaInvalida = new Joia(null, "Nome Joia", new BigDecimal("100.00"), null, fornecedorValido, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria da joia (com ID) não pode ser nula!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com categoria sem ID")
    void save_deveLancarExcecao_quandoCategoriaSemId() {
        // Arrange
        Categoria categoriaSemId = new Categoria(null, "Nova Categoria", null);
        Joia joiaInvalida = new Joia(null, "Nome Joia", new BigDecimal("100.00"), categoriaSemId, fornecedorValido, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A categoria da joia (com ID) não pode ser nula!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com fornecedor nulo")
    void save_deveLancarExcecao_quandoFornecedorNulo() {
        // Arrange
        Joia joiaInvalida = new Joia(null, "Nome Joia", new BigDecimal("100.00"), categoriaValida, null, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O fornecedor da joia (com ID) não pode ser nulo!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao salvar joia com fornecedor sem ID")
    void save_deveLancarExcecao_quandoFornecedorSemId() {
        // Arrange
        Fornecedor fornecedorSemId = new Fornecedor(null, "Novo Fornecedor", null);
        Joia joiaInvalida = new Joia(null, "Nome Joia", new BigDecimal("100.00"), categoriaValida, fornecedorSemId, null);

        // Act & Assert
        assertThatThrownBy(() -> joiaService.save(joiaInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O fornecedor da joia (com ID) não pode ser nulo!");
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    // --- Testes para findById ---

    @Test
    @DisplayName("[Unit Test] Deve retornar joia quando ID existe")
    void findById_deveRetornarJoia_quandoIdExiste() {
        // Arrange
        Long idExistente = joiaValida.getId();
        when(joiaRepository.findById(idExistente)).thenReturn(Optional.of(joiaValida));

        // Act
        Joia joiaEncontrada = joiaService.findById(idExistente);

        // Assert
        assertThat(joiaEncontrada).isNotNull();
        assertThat(joiaEncontrada.getId()).isEqualTo(idExistente);
        verify(joiaRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção quando ID não existe em findById")
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(joiaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> joiaService.findById(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Joia ID " + idInexistente + " não encontrada!");
        verify(joiaRepository, times(1)).findById(idInexistente);
    }

    // --- Testes para findAll ---

    @Test
    @DisplayName("[Unit Test] Deve retornar todas as joias")
    void findAll_deveRetornarTodasJoias() {
        // Arrange
        List<Joia> listaMock = List.of(joiaValida, joiaComPedidos);
        when(joiaRepository.findAll()).thenReturn(listaMock);

        // Act
        List<Joia> resultado = joiaService.findAll();

        // Assert
        assertThat(resultado).isNotNull().hasSize(2).containsExactly(joiaValida, joiaComPedidos);
        verify(joiaRepository, times(1)).findAll();
    }

    // --- Testes para update ---
    // (Similar aos testes de save, mas verificando findById antes e save depois)

    @Test
    @DisplayName("[Unit Test] Deve atualizar joia com sucesso")
    void update_deveAtualizarComSucesso() {
        // Arrange
        Long idParaAtualizar = joiaValida.getId();
        Categoria novaCategoria = new Categoria(2L, "Brincos", null);
        Fornecedor novoFornecedor = new Fornecedor(2L, "Prata Fina", null);
        Joia dadosAtualizacao = new Joia(idParaAtualizar, "Anel Atualizado", new BigDecimal("2000.00"), novaCategoria, novoFornecedor, null);

        when(joiaRepository.findById(idParaAtualizar)).thenReturn(Optional.of(joiaValida));
        when(joiaRepository.save(any(Joia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String resultado = joiaService.update(dadosAtualizacao, idParaAtualizar);

        // Assert
        assertThat(resultado).isEqualTo("Joia ID " + idParaAtualizar + " atualizada com sucesso!");
        verify(joiaRepository).findById(idParaAtualizar);
        verify(joiaRepository).save(argThat(j ->
                j.getId().equals(idParaAtualizar) &&
                        j.getNome().equals(dadosAtualizacao.getNome()) &&
                        j.getPreco().compareTo(dadosAtualizacao.getPreco()) == 0 &&
                        j.getCategoria().getId().equals(novaCategoria.getId()) &&
                        j.getFornecedor().getId().equals(novoFornecedor.getId())
        ));
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao atualizar joia com ID inexistente")
    void update_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        Joia dadosAtualizacao = new Joia(idInexistente, "Nome", new BigDecimal("100"), categoriaValida, fornecedorValido, null);
        when(joiaRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> joiaService.update(dadosAtualizacao, idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Joia ID " + idInexistente + " não encontrada!");
        verify(joiaRepository).findById(idInexistente);
        verify(joiaRepository, never()).save(any(Joia.class));
    }

    // Adicionar testes para validações de nome, preço, categoria e fornecedor no update (similar aos de save)


    // --- Testes para delete ---

    @Test
    @DisplayName("[Unit Test] Deve deletar joia com sucesso quando não tem pedidos associados")
    void delete_deveDeletarComSucesso_quandoNaoHaPedidos() {
        // Arrange
        Long idParaDeletar = joiaValida.getId(); // joiaValida foi criada sem pedidos
        when(joiaRepository.findById(idParaDeletar)).thenReturn(Optional.of(joiaValida));
        doNothing().when(joiaRepository).deleteById(idParaDeletar);

        // Act
        String resultado = joiaService.delete(idParaDeletar);

        // Assert
        assertThat(resultado).isEqualTo("Joia ID " + idParaDeletar + " deletada com sucesso!");
        verify(joiaRepository).findById(idParaDeletar);
        verify(joiaRepository).deleteById(idParaDeletar);
    }

    @Test
    @DisplayName("[Unit Test] Deve lançar exceção ao deletar joia com pedidos associados")
    void delete_deveLancarExcecao_quandoHaPedidosAssociados() {
        // Arrange
        Long idParaDeletar = joiaComPedidos.getId(); // Esta tem pedido mockado no setUp
        when(joiaRepository.findById(idParaDeletar)).thenReturn(Optional.of(joiaComPedidos));

        // Act & Assert
        assertThatThrownBy(() -> joiaService.delete(idParaDeletar))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é possível excluir a joia ID " + idParaDeletar + " pois ela está associada a");
        verify(joiaRepository).findById(idParaDeletar);
        verify(joiaRepository, never()).deleteById(anyLong());
    }

    // --- Testes para findAllByOrderByNomeAsc ---

    @Test
    @DisplayName("[Unit Test] Deve retornar joias ordenadas por nome ascendente")
    void findAllByOrderByNomeAsc_deveRetornarJoiasOrdenadasAsc() {
        // Arrange
        List<Joia> listaMockOrdenada = List.of(joiaValida, joiaComPedidos); // Exemplo, a ordem real viria do repo
        when(joiaRepository.findAllByOrderByNomeAsc()).thenReturn(listaMockOrdenada);

        // Act
        List<Joia> resultado = joiaService.findAllByOrderByNomeAsc();

        // Assert
        assertThat(resultado).isNotNull().isEqualTo(listaMockOrdenada);
        verify(joiaRepository).findAllByOrderByNomeAsc();
    }

    // --- Testes para filtrar ---

    @Test
    @DisplayName("[Unit Test] Deve chamar repositório filtrar com todos os parâmetros")
    void filtrar_deveChamarRepositorioComTodosParametros() {
        // Arrange
        String nome = "Anel";
        Long categoriaId = 1L;
        Long fornecedorId = 1L;
        BigDecimal precoMin = new BigDecimal("1000");
        List<Joia> listaMock = List.of(joiaValida);
        when(joiaRepository.filtrar(nome, categoriaId, fornecedorId, precoMin)).thenReturn(listaMock);

        // Act
        List<Joia> resultado = joiaService.filtrar(nome, categoriaId, fornecedorId, precoMin);

        // Assert
        assertThat(resultado).isEqualTo(listaMock);
        verify(joiaRepository).filtrar(eq(nome), eq(categoriaId), eq(fornecedorId), eq(precoMin));
    }

    @Test
    @DisplayName("[Unit Test] Deve chamar repositório filtrar com parâmetros nulos corretamente")
    void filtrar_deveChamarRepositorioComParametrosNulos() {
        // Arrange
        String nome = "Colar";
        Long categoriaId = null; // Categoria nula
        Long fornecedorId = 2L;
        BigDecimal precoMin = null; // Preço mínimo nulo
        List<Joia> listaMock = List.of(joiaComPedidos); // Exemplo
        // Espera que o service passe null para o repo onde o ID/Preco for nulo/inválido
        when(joiaRepository.filtrar(eq(nome), isNull(), eq(fornecedorId), isNull())).thenReturn(listaMock);

        // Act
        List<Joia> resultado = joiaService.filtrar(nome, categoriaId, fornecedorId, precoMin);

        // Assert
        assertThat(resultado).isEqualTo(listaMock);
        verify(joiaRepository).filtrar(eq(nome), isNull(), eq(fornecedorId), isNull());
    }

    @Test
    @DisplayName("[Unit Test] Deve chamar repositório filtrar com nome vazio/nulo corretamente (passando null)")
    void filtrar_deveChamarRepositorioComNomeNulo() {
        // Arrange
        Long categoriaId = 1L;
        Long fornecedorId = 1L;
        BigDecimal precoMin = BigDecimal.ZERO;
        List<Joia> listaMock = List.of(joiaValida);
        // Espera que o service passe null para o nome no repo
        when(joiaRepository.filtrar(isNull(), eq(categoriaId), eq(fornecedorId), eq(precoMin))).thenReturn(listaMock);

        // Act (testando com nome vazio)
        List<Joia> resultadoVazio = joiaService.filtrar("", categoriaId, fornecedorId, precoMin);
        // Assert
        assertThat(resultadoVazio).isEqualTo(listaMock);
        verify(joiaRepository).filtrar(isNull(), eq(categoriaId), eq(fornecedorId), eq(precoMin));

        // Act (testando com nome nulo)
        List<Joia> resultadoNulo = joiaService.filtrar(null, categoriaId, fornecedorId, precoMin);
        // Assert
        assertThat(resultadoNulo).isEqualTo(listaMock);
        // Verifica que a chamada foi feita novamente (total 2 vezes com nome nulo)
        verify(joiaRepository, times(2)).filtrar(isNull(), eq(categoriaId), eq(fornecedorId), eq(precoMin));
    }

    @Test
    @DisplayName("[Unit Test] Deve chamar repositório filtrar com ID de categoria inválido (passando null)")
    void filtrar_deveChamarRepositorioComCategoriaIdInvalido() {
        // Arrange
        String nome = "Joia";
        Long categoriaIdInvalido = 0L; // ID inválido
        Long fornecedorId = 1L;
        BigDecimal precoMin = BigDecimal.TEN;
        List<Joia> listaMock = Collections.emptyList();
        // Espera que o service passe null para categoriaId
        when(joiaRepository.filtrar(eq(nome), isNull(), eq(fornecedorId), eq(precoMin))).thenReturn(listaMock);

        // Act
        List<Joia> resultado = joiaService.filtrar(nome, categoriaIdInvalido, fornecedorId, precoMin);

        // Assert
        assertThat(resultado).isEqualTo(listaMock);
        verify(joiaRepository).filtrar(eq(nome), isNull(), eq(fornecedorId), eq(precoMin));
    }

    @Test
    @DisplayName("[Unit Test] Deve chamar repositório filtrar com preço mínimo negativo (passando null)")
    void filtrar_deveChamarRepositorioComPrecoMinNegativo() {
        // Arrange
        String nome = "Joia";
        Long categoriaId = 1L;
        Long fornecedorId = 1L;
        BigDecimal precoMinNegativo = new BigDecimal("-50"); // Preço inválido
        List<Joia> listaMock = Collections.emptyList();
        // Espera que o service passe null para precoMin
        when(joiaRepository.filtrar(eq(nome), eq(categoriaId), eq(fornecedorId), isNull())).thenReturn(listaMock);

        // Act
        List<Joia> resultado = joiaService.filtrar(nome, categoriaId, fornecedorId, precoMinNegativo);

        // Assert
        assertThat(resultado).isEqualTo(listaMock);
        verify(joiaRepository).filtrar(eq(nome), eq(categoriaId), eq(fornecedorId), isNull());
    }

}
