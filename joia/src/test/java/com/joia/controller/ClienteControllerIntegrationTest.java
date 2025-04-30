package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joia.entity.Cliente;
import com.joia.repository.ClienteRepository;
import com.joia.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @MockBean
    private ClienteService clienteServiceMock;

    private Cliente clienteExistente1;
    private Cliente clienteExistente2;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        clienteExistente1 = new Cliente(null, "Ana Silva Test", "ana.silva.test@email.com", new ArrayList<>());
        clienteExistente2 = new Cliente(null, "Fernanda Santos Test", "fernanda.santos.test@gmail.com", new ArrayList<>());

        // Salva alguns clientes diretamente no banco para testes de GET, PUT, DELETE que NÃO usam o mock
        clienteExistente1 = clienteRepository.save(clienteExistente1);
        clienteExistente2 = clienteRepository.save(clienteExistente2);
    }


    @Test
    @DisplayName("[Integration Test com Mock] POST /save - Deve criar cliente com sucesso")
    void save_deveCriarCliente_quandoDadosValidos() throws Exception {

        Cliente novoCliente = new Cliente(null, "Carlos Pereira", "carlos.pereira@email.com", null);
        String clienteJson = objectMapper.writeValueAsString(novoCliente);

        when(clienteServiceMock.save(any(Cliente.class))).thenReturn("Cliente salvo com sucesso!");

        mockMvc.perform(post("/api/clientes/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Cliente salvo com sucesso!"));

        verify(clienteServiceMock, times(1)).save(any(Cliente.class));

    }

    @Test
    @DisplayName("[Integration Test] POST /save - Deve retornar Bad Request ao salvar com nome vazio")
    void save_deveRetornarBadRequest_quandoNomeVazio() throws Exception {
        Cliente clienteInvalido = new Cliente(null, "", "invalido@email.com", null);
        String clienteJson = objectMapper.writeValueAsString(clienteInvalido);

        mockMvc.perform(post("/api/clientes/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest()) // Espera HTTP 400 Bad Request (devido à validação @NotBlank)
                .andExpect(jsonPath("$.nome").value("O nome do cliente não pode ser vazio.")); // Verifica a mensagem de erro da validação

        verify(clienteServiceMock, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("[Integration Test] POST /save - Deve retornar Bad Request ao salvar com email inválido")
    void save_deveRetornarBadRequest_quandoEmailInvalido() throws Exception {
        Cliente clienteInvalido = new Cliente(null, "Nome Valido", "email-invalido", null);
        String clienteJson = objectMapper.writeValueAsString(clienteInvalido);
        mockMvc.perform(post("/api/clientes/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("O email deve ser válido."));

        verify(clienteServiceMock, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("[Integration Test com Mock] POST /save - Deve retornar Bad Request ao salvar com email duplicado")
    void save_deveRetornarBadRequest_quandoEmailDuplicado() throws Exception {
        // Arrange
        Cliente clienteDuplicado = new Cliente(null, "Outro Nome", "email.duplicado@teste.com", null);
        String clienteJson = objectMapper.writeValueAsString(clienteDuplicado);
        String mensagemErro = "Email 'email.duplicado@teste.com' já cadastrado!";

        when(clienteServiceMock.save(any(Cliente.class))).thenThrow(new RuntimeException(mensagemErro));

        mockMvc.perform(post("/api/clientes/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest()) // RuntimeException é mapeada para 400 pelo GlobalExceptionHandler
                .andExpect(content().string(mensagemErro));

        verify(clienteServiceMock, times(1)).save(any(Cliente.class));
    }


    @Test
    @DisplayName("[Integration Test com Mock] GET /findAll - Deve retornar todos os clientes do serviço mockado")
    void findAll_deveRetornarTodosClientes() throws Exception {
        List<Cliente> listaMock = List.of(clienteExistente1, clienteExistente2);
        when(clienteServiceMock.findAll()).thenReturn(listaMock);
        mockMvc.perform(get("/api/clientes/findAll"))
                .andExpect(status().isOk()) // Espera HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)) // Verifica o tamanho da lista retornada pelo mock
                .andExpect(jsonPath("$[0].id").value(clienteExistente1.getId()))
                .andExpect(jsonPath("$[0].nome").value(clienteExistente1.getNome()))
                .andExpect(jsonPath("$[1].id").value(clienteExistente2.getId()))
                .andExpect(jsonPath("$[1].nome").value(clienteExistente2.getNome()));

        verify(clienteServiceMock, times(1)).findAll();
    }

    @Test
    @DisplayName("[Integration Test com Mock] GET /findById/{id} - Deve retornar cliente mockado quando ID existe")
    void findById_deveRetornarCliente_quandoServicoMockado() throws Exception {
        Long idExistente = 1L; // ID arbitrário para o teste
        Cliente clienteMockado = new Cliente(idExistente, "Cliente Mockado Via ID", "mockid@email.com", new ArrayList<>());
        when(clienteServiceMock.findById(idExistente)).thenReturn(clienteMockado);

        mockMvc.perform(get("/api/clientes/findById/{id}", idExistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(idExistente))
                .andExpect(jsonPath("$.nome").value("Cliente Mockado Via ID"))
                .andExpect(jsonPath("$.email").value("mockid@email.com"));

        verify(clienteServiceMock, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("[Integration Test com Mock] GET /findById/{id} - Deve retornar Bad Request quando serviço mockado lança exceção")
    void findById_deveRetornarBadRequest_quandoServicoMockadoLancaExcecao() throws Exception {
        Long idInexistente = 999L;
        String mensagemErro = "Cliente ID " + idInexistente + " não encontrado!";
        when(clienteServiceMock.findById(idInexistente)).thenThrow(new RuntimeException(mensagemErro));

        mockMvc.perform(get("/api/clientes/findById/{id}", idInexistente)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mensagemErro));

        verify(clienteServiceMock, times(1)).findById(idInexistente);
    }
    @Test
    @DisplayName("[Integration Test com Mock] PUT /update/{id} - Deve atualizar cliente com sucesso")
    void update_deveAtualizarCliente_quandoDadosValidos() throws Exception {
        Long idParaAtualizar = clienteExistente1.getId(); // Usa um ID que existiria no banco
        Cliente dadosUpdate = new Cliente(idParaAtualizar, "Ana Silva Atualizada Mock", "ana.mock.atualizada@email.com", null);
        String clienteJson = objectMapper.writeValueAsString(dadosUpdate);
        String mensagemSucesso = "Cliente ID " + idParaAtualizar + " atualizado com sucesso!";

        when(clienteServiceMock.update(any(Cliente.class), eq(idParaAtualizar))).thenReturn(mensagemSucesso);

        mockMvc.perform(put("/api/clientes/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        // Verifica se o método update do SERVIÇO MOCKADO foi chamado
        verify(clienteServiceMock, times(1)).update(any(Cliente.class), eq(idParaAtualizar));
    }

    @Test
    @DisplayName("[Integration Test com Mock] PUT /update/{id} - Deve retornar Bad Request se serviço mockado lança exceção (ID não existe)")
    void update_deveRetornarBadRequest_quandoServicoMockadoLancaExcecaoIdNaoExiste() throws Exception {
        Long idInexistente = 999L;
        Cliente dadosUpdate = new Cliente(idInexistente, "Nome Qualquer", "email.qualquer@email.com", null);
        String clienteJson = objectMapper.writeValueAsString(dadosUpdate);
        String mensagemErro = "Cliente ID " + idInexistente + " não encontrado!";

        when(clienteServiceMock.update(any(Cliente.class), eq(idInexistente))).thenThrow(new RuntimeException(mensagemErro));

        mockMvc.perform(put("/api/clientes/update/{id}", idInexistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mensagemErro));

        verify(clienteServiceMock, times(1)).update(any(Cliente.class), eq(idInexistente));
    }

    @Test
    @DisplayName("[Integration Test com Mock] DELETE /delete/{id} - Deve deletar cliente com sucesso")
    void delete_deveDeletarCliente_quandoServicoMockadoRetornaSucesso() throws Exception {
        Long idParaDeletar = clienteExistente1.getId(); // Usa um ID que existiria
        String mensagemSucesso = "Cliente ID " + idParaDeletar + " deletado com sucesso!";

        when(clienteServiceMock.delete(idParaDeletar)).thenReturn(mensagemSucesso);

        mockMvc.perform(delete("/api/clientes/delete/{id}", idParaDeletar))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(clienteServiceMock, times(1)).delete(idParaDeletar);
    }

    @Test
    @DisplayName("[Integration Test com Mock] DELETE /delete/{id} - Deve retornar Bad Request se serviço mockado lança exceção (ID não existe)")
    void delete_deveRetornarBadRequest_quandoServicoMockadoLancaExcecaoIdNaoExiste() throws Exception {
        Long idInexistente = 999L;
        String mensagemErro = "Cliente ID " + idInexistente + " não encontrado!";

        when(clienteServiceMock.delete(idInexistente)).thenThrow(new RuntimeException(mensagemErro));

        mockMvc.perform(delete("/api/clientes/delete/{id}", idInexistente))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(mensagemErro));

        verify(clienteServiceMock, times(1)).delete(idInexistente);
    }


    @Test
    @DisplayName("[Integration Test com Mock] GET /findByNomeContaining - Deve retornar clientes correspondentes do serviço mockado")
    void findByNomeContaining_deveRetornarClientesFiltrados() throws Exception {
        String termoBusca = "Ana";
        List<Cliente> listaMock = List.of(clienteExistente1);
        when(clienteServiceMock.findByNomeContaining(termoBusca)).thenReturn(listaMock);

        mockMvc.perform(get("/api/clientes/findByNomeContaining")
                        .param("nome", termoBusca))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value(clienteExistente1.getNome()));

        verify(clienteServiceMock, times(1)).findByNomeContaining(termoBusca);
    }

}
