package com.joia.service;

import com.joia.entity.Cliente;
import com.joia.entity.Pedido; // Import necessário se usar getPedidos()
import com.joia.repository.ClienteRepository;
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
class ClienteServiceTest {

    @InjectMocks
    private ClienteService clienteService;

    @Mock
    private ClienteRepository clienteRepository;

    private Cliente clienteValido;
    private Cliente clienteExistente;
    private Cliente clienteSemPedidos;

    @BeforeEach
    void setUp() {
        // Cria clientes reutilizáveis para os testes
        clienteValido = new Cliente(1L, "Cliente Teste", "teste@email.com", new ArrayList<>());
        clienteExistente = new Cliente(2L, "Cliente Existente", "existente@email.com", new ArrayList<>());
        clienteSemPedidos = new Cliente(3L, "Cliente Sem Pedidos", "sempedidos@email.com", new ArrayList<>());

        // Adiciona um pedido simulado ao clienteExistente para testes específicos
        Pedido pedidoMock = new Pedido(); // Pode adicionar mais detalhes se necessário
        clienteExistente.getPedidos().add(pedidoMock);
    }

    // --- Testes para o método save ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve salvar cliente com sucesso quando dados válidos e email não existe")
    void save_deveSalvarComSucesso_quandoDadosValidos() {
        // Arrange
        Cliente novoCliente = new Cliente(null, "Novo Cliente", "novo@email.com", null);
        Cliente clienteSalvo = new Cliente(1L, "Novo Cliente", "novo@email.com", new ArrayList<>());
        when(clienteRepository.findByEmail(novoCliente.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        // Act
        String resultado = clienteService.save(novoCliente);

        // Assert
        assertThat(resultado).isEqualTo("Cliente salvo com sucesso!");
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(clienteRepository, times(1)).findByEmail(novoCliente.getEmail());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao salvar cliente com email já existente")
    void save_deveLancarExcecao_quandoEmailJaExiste() {
        // Arrange
        Cliente novoCliente = new Cliente(null, "Novo Cliente", clienteExistente.getEmail(), null);
        when(clienteRepository.findByEmail(novoCliente.getEmail())).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> clienteService.save(novoCliente))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email '" + novoCliente.getEmail() + "' já cadastrado!");
        verify(clienteRepository, never()).save(any(Cliente.class));
        verify(clienteRepository, times(1)).findByEmail(novoCliente.getEmail());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao salvar cliente com nome vazio")
    void save_deveLancarExcecao_quandoNomeVazio() {
        // Arrange
        Cliente clienteInvalido = new Cliente(null, "", "invalido@email.com", null);

        // Act & Assert
        assertThatThrownBy(() -> clienteService.save(clienteInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome do cliente não pode ser vazio.");
        verifyNoInteractions(clienteRepository); // Verifica que não houve interação com o repositório
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao salvar cliente com email vazio")
    void save_deveLancarExcecao_quandoEmailVazio() {
        // Arrange
        Cliente clienteInvalido = new Cliente(null, "Nome Valido", "", null);

        // Act & Assert
        assertThatThrownBy(() -> clienteService.save(clienteInvalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O email do cliente não pode ser vazio.");
        verifyNoInteractions(clienteRepository);
    }

    // --- Testes para o método findById ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar cliente quando ID existe")
    void findById_deveRetornarCliente_quandoIdExiste() {
        // Arrange
        Long idExistente = clienteValido.getId();
        when(clienteRepository.findById(idExistente)).thenReturn(Optional.of(clienteValido));

        // Act
        Cliente clienteEncontrado = clienteService.findById(idExistente);

        // Assert
        assertThat(clienteEncontrado).isNotNull();
        assertThat(clienteEncontrado.getId()).isEqualTo(idExistente);
        assertThat(clienteEncontrado.getNome()).isEqualTo(clienteValido.getNome());
        verify(clienteRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção quando ID não existe em findById")
    void findById_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.findById(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente ID " + idInexistente + " não encontrado!");
        verify(clienteRepository, times(1)).findById(idInexistente);
    }

    // --- Testes para o método findAll ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista de todos os clientes")
    void findAll_deveRetornarTodosClientes() {
        // Arrange
        List<Cliente> listaClientes = List.of(clienteValido, clienteExistente);
        when(clienteRepository.findAll()).thenReturn(listaClientes);

        // Act
        List<Cliente> resultado = clienteService.findAll();

        // Assert
        assertThat(resultado).isNotNull().hasSize(2).containsExactly(clienteValido, clienteExistente);
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia quando não há clientes")
    void findAll_deveRetornarListaVazia_quandoNaoHaClientes() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Cliente> resultado = clienteService.findAll();

        // Assert
        assertThat(resultado).isNotNull().isEmpty();
        verify(clienteRepository, times(1)).findAll();
    }

    // --- Testes para o método update ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve atualizar cliente com sucesso")
    void update_deveAtualizarComSucesso() {
        // Arrange
        Long idParaAtualizar = clienteValido.getId();
        Cliente clienteAtualizadoDados = new Cliente(idParaAtualizar, "Nome Atualizado", "novoemail@email.com", null);
        when(clienteRepository.findById(idParaAtualizar)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.findByEmail(clienteAtualizadoDados.getEmail())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String resultado = clienteService.update(clienteAtualizadoDados, idParaAtualizar);

        // Assert
        assertThat(resultado).isEqualTo("Cliente ID " + idParaAtualizar + " atualizado com sucesso!");
        verify(clienteRepository).findById(idParaAtualizar);
        verify(clienteRepository).findByEmail(clienteAtualizadoDados.getEmail());
        verify(clienteRepository).save(argThat(cliente ->
                cliente.getId().equals(idParaAtualizar) &&
                        cliente.getNome().equals(clienteAtualizadoDados.getNome()) &&
                        cliente.getEmail().equals(clienteAtualizadoDados.getEmail())
        ));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao atualizar se ID não existe")
    void update_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        Cliente clienteAtualizadoDados = new Cliente(idInexistente, "Nome", "email@email.com", null);
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.update(clienteAtualizadoDados, idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente ID " + idInexistente + " não encontrado!");
        verify(clienteRepository, times(1)).findById(idInexistente);
        verify(clienteRepository, never()).findByEmail(anyString());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao atualizar para email que já pertence a OUTRO cliente")
    void update_deveLancarExcecao_quandoNovoEmailJaExisteEmOutroCliente() {
        // Arrange
        Long idParaAtualizar = clienteValido.getId(); // ID 1
        String emailConflitante = clienteExistente.getEmail(); // email do cliente com ID 2
        Cliente clienteComDadosAtualizados = new Cliente(idParaAtualizar, "Nome Atualizado", emailConflitante, null);
        when(clienteRepository.findById(idParaAtualizar)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.findByEmail(emailConflitante)).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> clienteService.update(clienteComDadosAtualizados, idParaAtualizar))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email '" + emailConflitante + "' já cadastrado para outro cliente!");
        verify(clienteRepository).findById(idParaAtualizar);
        verify(clienteRepository).findByEmail(emailConflitante);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve permitir atualizar cliente mantendo o mesmo email")
    void update_devePermitirAtualizar_mantendoMesmoEmail() {
        // Arrange
        Long idParaAtualizar = clienteValido.getId();
        String mesmoEmail = clienteValido.getEmail();
        Cliente clienteComNomeAtualizado = new Cliente(idParaAtualizar, "Nome Atualizado", mesmoEmail, null);
        when(clienteRepository.findById(idParaAtualizar)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.findByEmail(mesmoEmail)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String resultado = clienteService.update(clienteComNomeAtualizado, idParaAtualizar);

        // Assert
        assertThat(resultado).isEqualTo("Cliente ID " + idParaAtualizar + " atualizado com sucesso!");
        verify(clienteRepository).findById(idParaAtualizar);
        verify(clienteRepository).findByEmail(mesmoEmail);
        verify(clienteRepository).save(argThat(c -> c.getNome().equals("Nome Atualizado")));
    }

    // --- Testes para o método delete ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve deletar cliente com sucesso quando não há pedidos associados")
    void delete_deveDeletarComSucesso_quandoClienteNaoTemPedidos() {
        // Arrange
        Long idParaDeletar = clienteSemPedidos.getId();
        when(clienteRepository.findById(idParaDeletar)).thenReturn(Optional.of(clienteSemPedidos));
        doNothing().when(clienteRepository).deleteById(idParaDeletar);

        // Act
        String resultado = clienteService.delete(idParaDeletar);

        // Assert
        assertThat(resultado).isEqualTo("Cliente ID " + idParaDeletar + " deletado com sucesso!");
        verify(clienteRepository).findById(idParaDeletar);
        verify(clienteRepository).deleteById(idParaDeletar);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao deletar cliente com pedidos associados")
    void delete_deveLancarExcecao_quandoClienteTemPedidos() {
        // Arrange
        Long idParaDeletar = clienteExistente.getId(); // Este cliente tem um pedido mockado no setUp
        when(clienteRepository.findById(idParaDeletar)).thenReturn(Optional.of(clienteExistente));

        // Act & Assert
        assertThatThrownBy(() -> clienteService.delete(idParaDeletar))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Não é possível excluir o cliente ID " + idParaDeletar + " pois ele possui");
        verify(clienteRepository).findById(idParaDeletar);
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao deletar cliente com ID inexistente")
    void delete_deveLancarExcecao_quandoIdNaoExiste() {
        // Arrange
        Long idInexistente = 99L;
        when(clienteRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.delete(idInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente ID " + idInexistente + " não encontrado!");
        verify(clienteRepository).findById(idInexistente);
        verify(clienteRepository, never()).deleteById(anyLong());
    }

    // --- Testes para findByEmail ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar cliente ao buscar por email existente")
    void findByEmail_deveRetornarCliente_quandoEmailExiste() {
        // Arrange
        String emailExistente = clienteValido.getEmail();
        when(clienteRepository.findByEmail(emailExistente)).thenReturn(Optional.of(clienteValido));

        // Act
        Cliente clienteEncontrado = clienteService.findByEmail(emailExistente);

        // Assert
        assertThat(clienteEncontrado).isNotNull();
        assertThat(clienteEncontrado.getEmail()).isEqualTo(emailExistente);
        verify(clienteRepository, times(1)).findByEmail(emailExistente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao buscar por email inexistente")
    void findByEmail_deveLancarExcecao_quandoEmailNaoExiste() {
        // Arrange
        String emailInexistente = "naoexiste@email.com";
        when(clienteRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> clienteService.findByEmail(emailInexistente))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente com email '" + emailInexistente + "' não encontrado!"); // <-- Linha corrigida
        verify(clienteRepository, times(1)).findByEmail(emailInexistente);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve lançar exceção ao buscar por email vazio")
    void findByEmail_deveLancarExcecao_quandoEmailVazio() {
        // Arrange
        String emailVazio = "";

        // Act & Assert
        assertThatThrownBy(() -> clienteService.findByEmail(emailVazio))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email para busca não pode ser vazio.");
        verify(clienteRepository, never()).findByEmail(anyString()); // Não deve chamar o repositório
    }

    // --- Testes para findByNomeContaining ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista de clientes ao buscar por parte do nome")
    void findByNomeContaining_deveRetornarClientesCorrespondentes() {
        // Arrange
        String termoBusca = "Test";
        List<Cliente> listaMock = List.of(clienteValido); // Apenas clienteValido contém "Test"
        when(clienteRepository.findByNomeContainingIgnoreCase(termoBusca)).thenReturn(listaMock);

        // Act
        List<Cliente> resultado = clienteService.findByNomeContaining(termoBusca);

        // Assert
        assertThat(resultado).isNotNull().hasSize(1).containsExactly(clienteValido);
        verify(clienteRepository, times(1)).findByNomeContainingIgnoreCase(termoBusca);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia ao buscar por nome inexistente")
    void findByNomeContaining_deveRetornarListaVazia_quandoNomeNaoEncontrado() {
        // Arrange
        String termoBusca = "Inexistente";
        when(clienteRepository.findByNomeContainingIgnoreCase(termoBusca)).thenReturn(Collections.emptyList());

        // Act
        List<Cliente> resultado = clienteService.findByNomeContaining(termoBusca);

        // Assert
        assertThat(resultado).isNotNull().isEmpty();
        verify(clienteRepository, times(1)).findByNomeContainingIgnoreCase(termoBusca);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar lista vazia ao buscar por nome vazio ou nulo")
    void findByNomeContaining_deveRetornarListaVazia_quandoNomeVazioOuNulo() {
        // Arrange (Teste com string vazia)
        List<Cliente> resultadoVazio = clienteService.findByNomeContaining("");
        // Assert
        assertThat(resultadoVazio).isNotNull().isEmpty();

        // Arrange (Teste com null)
        List<Cliente> resultadoNulo = clienteService.findByNomeContaining(null);
        // Assert
        assertThat(resultadoNulo).isNotNull().isEmpty();

        // Verifica que o repositório não foi chamado em nenhum dos casos
        verify(clienteRepository, never()).findByNomeContainingIgnoreCase(anyString());
    }

    // --- Testes para findByPedidosIsNotEmpty ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar clientes que possuem pedidos")
    void findByPedidosIsNotEmpty_deveRetornarClientesComPedidos() {
        // Arrange
        // clienteExistente foi configurado com um pedido no setUp
        List<Cliente> listaMock = List.of(clienteExistente);
        when(clienteRepository.findByPedidosIsNotEmpty()).thenReturn(listaMock);

        // Act
        List<Cliente> resultado = clienteService.findByPedidosIsNotEmpty();

        // Assert
        assertThat(resultado).isNotNull().hasSize(1).containsExactly(clienteExistente);
        verify(clienteRepository, times(1)).findByPedidosIsNotEmpty();
    }

    // --- Testes para findAllByOrderByNomeDesc ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar clientes ordenados por nome descendente")
    void findAllByOrderByNomeDesc_deveRetornarClientesOrdenadosDesc() {
        // Arrange
        // A ordem correta seria clienteValido ("Cliente Teste") e depois clienteExistente ("Cliente Existente")
        List<Cliente> listaMockOrdenada = List.of(clienteValido, clienteExistente);
        when(clienteRepository.findAllByOrderByNomeDesc()).thenReturn(listaMockOrdenada);

        // Act
        List<Cliente> resultado = clienteService.findAllByOrderByNomeDesc();

        // Assert
        assertThat(resultado).isNotNull().hasSize(2).containsExactly(clienteValido, clienteExistente);
        verify(clienteRepository, times(1)).findAllByOrderByNomeDesc();
    }

    // --- Testes para findClientesSemPedidos ---
    @Test
    @DisplayName("TESTE DE UNIDADE – Deve retornar clientes que não possuem pedidos")
    void findClientesSemPedidos_deveRetornarClientesSemPedidos() {
        // Arrange
        // clienteValido e clienteSemPedidos foram configurados sem pedidos no setUp
        List<Cliente> listaMock = List.of(clienteValido, clienteSemPedidos);
        when(clienteRepository.findClientesSemPedidos()).thenReturn(listaMock);

        // Act
        List<Cliente> resultado = clienteService.findClientesSemPedidos();

        // Assert
        assertThat(resultado).isNotNull().hasSize(2).containsExactly(clienteValido, clienteSemPedidos);
        verify(clienteRepository, times(1)).findClientesSemPedidos();
    }
}