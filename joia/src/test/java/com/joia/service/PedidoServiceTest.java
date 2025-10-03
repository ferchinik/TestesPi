package com.joia.service;

import com.joia.dto.FaturamentoDTO;
import com.joia.entity.Categoria;
import com.joia.entity.Cliente;
import com.joia.entity.Fornecedor;
import com.joia.entity.Joia;
import com.joia.entity.Pedido;
import com.joia.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    private Pedido pedidoValido;
    private Cliente clienteValido;
    private Joia joiaValida1;
    private Joia joiaValida2;
    private Date dataPedidoValida;

    @BeforeEach
    void setUp() {
        dataPedidoValida = new Date();
        clienteValido = new Cliente(1L, "Cliente Teste", "cliente@teste.com", new ArrayList<>());

        Categoria categoriaMock = new Categoria(1L, "Anel", new ArrayList<>());
        Fornecedor fornecedorMock = new Fornecedor(1L, "Fornecedor Joias", new ArrayList<>());

        joiaValida1 = new Joia(1L, "Joia Teste 1", new BigDecimal("100.00"), categoriaMock, fornecedorMock, new ArrayList<>());
        joiaValida2 = new Joia(2L, "Joia Teste 2", new BigDecimal("200.00"), categoriaMock, fornecedorMock, new ArrayList<>());

        pedidoValido = new Pedido(1L, dataPedidoValida, clienteValido, new ArrayList<>(List.of(joiaValida1)));
    }

    // --- Testes para o método save ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Salvar pedido com sucesso")
    void save_deveSalvarComSucesso_quandoDadosValidos() {
        // Arrange
        Pedido novoPedido = new Pedido(null, dataPedidoValida, clienteValido, List.of(joiaValida1));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoValido);

        // Act
        String resultado = pedidoService.save(novoPedido);

        // Assert
        assertThat(resultado).isEqualTo("Pedido salvo com sucesso!");
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao salvar pedido com data nula")
    void save_deveLancarExcecao_quandoDataPedidoNula() {
        // Arrange
        Pedido pedidoInvalido = new Pedido(null, null, clienteValido, List.of(joiaValida1));

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.save(pedidoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data do pedido não pode ser nula!");
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao salvar pedido com cliente nulo")
    void save_deveLancarExcecao_quandoClienteNulo() {
        // Arrange
        Pedido pedidoInvalido = new Pedido(null, dataPedidoValida, null, List.of(joiaValida1));
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.save(pedidoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O cliente (com ID) do pedido não pode ser nulo!");
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao salvar pedido com cliente sem ID")
    void save_deveLancarExcecao_quandoClienteSemId() {
        // Arrange
        Cliente clienteSemId = new Cliente(null, "Cliente Sem ID", "semid@teste.com", new ArrayList<>());
        Pedido pedidoInvalido = new Pedido(null, dataPedidoValida, clienteSemId, List.of(joiaValida1));
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.save(pedidoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O cliente (com ID) do pedido não pode ser nulo!");
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao salvar pedido sem joias")
    void save_deveLancarExcecao_quandoListaJoiasVazia() {
        // Arrange
        Pedido pedidoInvalido = new Pedido(null, dataPedidoValida, clienteValido, new ArrayList<>());
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.save(pedidoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O pedido deve conter pelo menos uma joia!");
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao salvar pedido com lista de joias nula")
    void save_deveLancarExcecao_quandoListaJoiasNula() {
        // Arrange
        Pedido pedidoInvalido = new Pedido(null, dataPedidoValida, clienteValido, null);
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.save(pedidoInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O pedido deve conter pelo menos uma joia!");
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }


    // --- Testes para o método findById ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Encontrar pedido por ID existente")
    void findById_deveRetornarPedido_quandoIdExiste() {
        // Arrange
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoValido));

        // Act
        Pedido pedidoEncontrado = pedidoService.findById(1L);

        // Assert
        assertThat(pedidoEncontrado).isNotNull();
        assertThat(pedidoEncontrado.getId()).isEqualTo(1L);
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao buscar pedido por ID inexistente")
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido ID 99 não encontrado!");
        verify(pedidoRepository, times(1)).findById(99L);
    }

    // --- Testes para findAll ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar todos os pedidos")
    void findAll_deveRetornarTodosPedidos() {
        // Arrange
        List<Pedido> listaPedidos = List.of(pedidoValido, new Pedido(2L, new Date(), clienteValido, List.of(joiaValida1)));
        when(pedidoRepository.findAll()).thenReturn(listaPedidos);

        // Act
        List<Pedido> resultado = pedidoService.findAll();

        // Assert
        assertThat(resultado).hasSize(2);
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia quando não há pedidos")
    void findAll_deveRetornarListaVazia_quandoNaoHaPedidos() {
        // Arrange
        when(pedidoRepository.findAll()).thenReturn(Collections.emptyList());
        // Act
        List<Pedido> resultado = pedidoService.findAll();
        // Assert
        assertThat(resultado).isEmpty();
        verify(pedidoRepository, times(1)).findAll();
    }


    // --- Testes para update ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Atualizar pedido com sucesso")
    void update_deveAtualizarComSucesso_quandoDadosValidosEIdExistente() {
        // Arrange
        Long idExistente = 1L;
        Cliente novoCliente = new Cliente(2L, "Outro Cliente", "outro@teste.com", new ArrayList<>());
        Date novaData = new Date(System.currentTimeMillis() + 10000); // data diferente
        Pedido dadosAtualizacao = new Pedido(idExistente, novaData, novoCliente, List.of(joiaValida1, joiaValida2));

        Pedido pedidoOriginal = new Pedido(idExistente, dataPedidoValida, clienteValido, new ArrayList<>(List.of(joiaValida1))); // Clone para não modificar o `pedidoValido` original

        when(pedidoRepository.findById(idExistente)).thenReturn(Optional.of(pedidoOriginal));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String resultado = pedidoService.update(dadosAtualizacao, idExistente);

        // Assert
        assertThat(resultado).isEqualTo("Pedido ID " + idExistente + " atualizado com sucesso!");
        verify(pedidoRepository).findById(idExistente);
        verify(pedidoRepository).save(argThat(p ->
                p.getCliente().getId().equals(novoCliente.getId()) &&
                        p.getDataPedido().equals(novaData) &&
                        p.getJoias().size() == 2 &&
                        p.getJoias().contains(joiaValida1) &&
                        p.getJoias().contains(joiaValida2)
        ));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao atualizar pedido com ID inexistente")
    void update_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        Pedido dadosAtualizacao = new Pedido(idInexistente, dataPedidoValida, clienteValido, List.of(joiaValida1));
        when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.update(dadosAtualizacao, idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido ID " + idInexistente + " não encontrado!");
        verify(pedidoRepository, times(1)).findById(idInexistente);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao atualizar pedido com data nula")
    void update_deveLancarExcecao_quandoDataPedidoNula() {
        // Arrange
        Long idExistente = 1L;
        Pedido dadosAtualizacao = new Pedido(idExistente, null, clienteValido, List.of(joiaValida1));
        when(pedidoRepository.findById(idExistente)).thenReturn(Optional.of(pedidoValido));

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.update(dadosAtualizacao, idExistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data do pedido não pode ser nula!");
        verify(pedidoRepository, times(1)).findById(idExistente); // Verifica se tentou buscar
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao atualizar pedido com cliente nulo")
    void update_deveLancarExcecao_quandoClienteNulo() {
        // Arrange
        Long idExistente = 1L;
        Pedido dadosAtualizacao = new Pedido(idExistente, dataPedidoValida, null, List.of(joiaValida1));
        when(pedidoRepository.findById(idExistente)).thenReturn(Optional.of(pedidoValido));

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.update(dadosAtualizacao, idExistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O cliente (com ID) do pedido não pode ser nulo!");
        verify(pedidoRepository, times(1)).findById(idExistente);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao atualizar pedido com lista de joias vazia")
    void update_deveLancarExcecao_quandoListaJoiasVazia() {
        // Arrange
        Long idExistente = 1L;
        Pedido dadosAtualizacao = new Pedido(idExistente, dataPedidoValida, clienteValido, new ArrayList<>());
        when(pedidoRepository.findById(idExistente)).thenReturn(Optional.of(pedidoValido));

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.update(dadosAtualizacao, idExistente))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O pedido deve conter pelo menos uma joia!");
        verify(pedidoRepository, times(1)).findById(idExistente);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }


    // --- Testes para delete ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deletar pedido com sucesso")
    void delete_deveDeletarComSucesso_quandoIdExiste() {
        // Arrange
        Long idExistente = 1L;
        when(pedidoRepository.findById(idExistente)).thenReturn(Optional.of(pedidoValido));
        doNothing().when(pedidoRepository).deleteById(idExistente);

        // Act
        String resultado = pedidoService.delete(idExistente);

        // Assert
        assertThat(resultado).isEqualTo("Pedido ID " + idExistente + " deletado com sucesso!");
        verify(pedidoRepository).findById(idExistente);
        verify(pedidoRepository).deleteById(idExistente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao deletar pedido com ID inexistente")
    void delete_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(pedidoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.delete(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido ID " + idInexistente + " não encontrado!");
        verify(pedidoRepository, times(1)).findById(idInexistente);
        verify(pedidoRepository, never()).deleteById(anyLong());
    }

    // --- Testes para findByDataPedido ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve encontrar pedidos por data")
    void findByDataPedido_deveRetornarPedidosCorrespondentes() {
        // Arrange
        List<Pedido> listaMock = List.of(pedidoValido);
        when(pedidoRepository.findByDataPedido(any(Date.class))).thenReturn(listaMock);

        // Act
        List<Pedido> resultado = pedidoService.findByDataPedido(dataPedidoValida);

        // Assert
        assertThat(resultado).isNotEmpty().containsExactly(pedidoValido);
        verify(pedidoRepository).findByDataPedido(dataPedidoValida);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia se não encontrar pedidos para a data")
    void findByDataPedido_deveRetornarListaVazia_quandoNaoHaPedidosParaData() {
        // Arrange
        when(pedidoRepository.findByDataPedido(any(Date.class))).thenReturn(Collections.emptyList());
        // Act
        List<Pedido> resultado = pedidoService.findByDataPedido(dataPedidoValida);
        // Assert
        assertThat(resultado).isEmpty();
        verify(pedidoRepository).findByDataPedido(dataPedidoValida);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao buscar pedido com data nula em findByDataPedido")
    void findByDataPedido_deveLancarExcecao_quandoDataNula() {
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.findByDataPedido(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data para busca não pode ser nula.");
        verify(pedidoRepository, never()).findByDataPedido(any());
    }

    // --- Testes para findByDataPedidoBetween ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve encontrar pedidos entre datas")
    void findByDataPedidoBetween_deveRetornarPedidosNoIntervalo() {
        // Arrange
        Date dataInicio = new Date(System.currentTimeMillis() - 100000);
        Date dataFim = new Date();
        List<Pedido> listaMock = List.of(pedidoValido);
        when(pedidoRepository.findByDataPedidoBetween(dataInicio, dataFim)).thenReturn(listaMock);

        // Act
        List<Pedido> resultado = pedidoService.findByDataPedidoBetween(dataInicio, dataFim);

        // Assert
        assertThat(resultado).isNotEmpty().containsExactly(pedidoValido);
        verify(pedidoRepository).findByDataPedidoBetween(dataInicio, dataFim);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao buscar pedido entre datas com data de início nula")
    void findByDataPedidoBetween_deveLancarExcecao_quandoDataInicioNula() {
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.findByDataPedidoBetween(null, new Date()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Datas de início e fim são obrigatórias para busca por período.");
        verify(pedidoRepository, never()).findByDataPedidoBetween(any(), any());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao buscar pedido entre datas com data de fim nula")
    void findByDataPedidoBetween_deveLancarExcecao_quandoDataFimNula() {
        // Act & Assert
        assertThatThrownBy(() -> pedidoService.findByDataPedidoBetween(new Date(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Datas de início e fim são obrigatórias para busca por período.");
        verify(pedidoRepository, never()).findByDataPedidoBetween(any(), any());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Lançar exceção ao buscar pedido entre datas com data de início posterior à data de fim")
    void findByDataPedidoBetween_deveLancarExcecao_quandoDataInicioPosteriorDataFim() {
        // Arrange
        Date dataFim = new Date();
        // Calendar para garantir que a data de início seja realmente posterior
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataFim);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dataInicio = cal.getTime();


        // Act & Assert
        assertThatThrownBy(() -> pedidoService.findByDataPedidoBetween(dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data de início não pode ser posterior à data de fim.");
        verify(pedidoRepository, never()).findByDataPedidoBetween(any(), any());
    }

    // --- Testes para findByClienteNomeContaining ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve encontrar pedidos pelo nome do cliente")
    void findByClienteNomeContaining_deveRetornarPedidos() {
        // Arrange
        String nomeCliente = "Teste";
        List<Pedido> listaMock = List.of(pedidoValido);
        when(pedidoRepository.findByClienteNomeContainingIgnoreCase(nomeCliente)).thenReturn(listaMock);

        // Act
        List<Pedido> resultado = pedidoService.findByClienteNomeContaining(nomeCliente);

        // Assert
        assertThat(resultado).isNotEmpty().containsExactly(pedidoValido);
        verify(pedidoRepository).findByClienteNomeContainingIgnoreCase(nomeCliente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia ao buscar pedidos por nome de cliente vazio")
    void findByClienteNomeContaining_deveRetornarListaVazia_quandoNomeClienteVazio() {
        // Act
        List<Pedido> resultado = pedidoService.findByClienteNomeContaining("");
        // Assert
        assertThat(resultado).isEmpty();
        verify(pedidoRepository, never()).findByClienteNomeContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia ao buscar pedidos por nome de cliente nulo")
    void findByClienteNomeContaining_deveRetornarListaVazia_quandoNomeClienteNulo() {
        // Act
        List<Pedido> resultado = pedidoService.findByClienteNomeContaining(null);
        // Assert
        assertThat(resultado).isEmpty();
        verify(pedidoRepository, never()).findByClienteNomeContainingIgnoreCase(anyString());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Calcular Faturamento Total com Sucesso")
    void calcularFaturamentoTotal_quandoHaPedidosComJoias_deveRetornarSomaCorreta() {
        // Arrange
        Joia joia1 = new Joia();
        joia1.setId(1L);
        joia1.setNome("Colar de Ouro");
        joia1.setPreco(new BigDecimal("1200.00"));

        Joia joia2 = new Joia();
        joia2.setId(2L);
        joia2.setNome("Anel de Prata");
        joia2.setPreco(new BigDecimal("350.50"));

        Joia joia3 = new Joia(); // Joia sem preço para testar robustez
        joia3.setId(3L);
        joia3.setNome("Brinco Simples");
        joia3.setPreco(null);


        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setJoias(Arrays.asList(joia1, joia2)); // Joias com preço

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setJoias(Collections.singletonList(joia3)); // Joia sem preço

        Pedido pedido3 = new Pedido(); // Pedido sem joias
        pedido3.setId(3L);
        pedido3.setJoias(new ArrayList<>());


        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2, pedido3);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        FaturamentoDTO faturamentoDTO = pedidoService.calcularFaturamentoTotal();

        // Assert
        assertNotNull(faturamentoDTO);
        // Esperado: 1200.00 + 350.50 = 1550.50
        assertEquals(new BigDecimal("1550.50"), faturamentoDTO.getValorTotal());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Calcular Faturamento Total sem Pedidos")
    void calcularFaturamentoTotal_quandoNaoHaPedidos_deveRetornarZero() {
        // Arrange
        when(pedidoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        FaturamentoDTO faturamentoDTO = pedidoService.calcularFaturamentoTotal();

        // Assert
        assertNotNull(faturamentoDTO);
        assertEquals(BigDecimal.ZERO, faturamentoDTO.getValorTotal());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Calcular Faturamento Total com Pedidos sem Joias")
    void calcularFaturamentoTotal_quandoPedidosNaoTemJoias_deveRetornarZero() {
        // Arrange
        Pedido pedido1 = new Pedido();
        pedido1.setId(1L);
        pedido1.setJoias(new ArrayList<>()); // Lista de joias vazia

        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setJoias(null); // Lista de joias nula

        List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        FaturamentoDTO faturamentoDTO = pedidoService.calcularFaturamentoTotal();

        // Assert
        assertNotNull(faturamentoDTO);
        assertEquals(BigDecimal.ZERO, faturamentoDTO.getValorTotal());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Calcular Faturamento Total com Joias com Preço Nulo")
    void calcularFaturamentoTotal_quandoJoiasTemPrecoNulo_deveIgnorarPrecosNulos() {
        // Arrange
        Joia joiaComPreco = new Joia();
        joiaComPreco.setPreco(new BigDecimal("100.00"));

        Joia joiaSemPreco = new Joia();
        joiaSemPreco.setPreco(null);

        Pedido pedido = new Pedido();
        pedido.setJoias(Arrays.asList(joiaComPreco, joiaSemPreco));

        when(pedidoRepository.findAll()).thenReturn(Collections.singletonList(pedido));

        // Act
        FaturamentoDTO faturamentoDTO = pedidoService.calcularFaturamentoTotal();

        // Assert
        assertNotNull(faturamentoDTO);
        assertEquals(new BigDecimal("100.00"), faturamentoDTO.getValorTotal());
    }
}