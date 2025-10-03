package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.joia.entity.Categoria;
import com.joia.entity.Cliente;
import com.joia.entity.Fornecedor;
import com.joia.entity.Joia;
import com.joia.entity.Pedido;
import com.joia.service.PedidoService;
import com.joia.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private Pedido pedidoValido1;
    private Pedido pedidoValido2;
    private Cliente clienteValido;
    private Joia joiaValida;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);

        clienteValido = new Cliente(1L, "Cliente Mock", "mock@cliente.com", new ArrayList<>());
        Categoria catMock = new Categoria(1L, "Mock Categoria", new ArrayList<>());
        Fornecedor fornMock = new Fornecedor(1L, "Mock Fornecedor", new ArrayList<>());
        joiaValida = new Joia(1L, "Joia Mock", new BigDecimal("250.00"), catMock, fornMock, new ArrayList<>());

        Date data1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data1);
        cal.add(Calendar.DAY_OF_MONTH, -5);
        Date data2 = cal.getTime();

        pedidoValido1 = new Pedido(1L, data1, clienteValido, List.of(joiaValida));
        pedidoValido2 = new Pedido(2L, data2, clienteValido, List.of(joiaValida));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve criar pedido com sucesso com ROLE_USER")
    @WithMockUser(username = "testuser")
    void save_deveCriarPedido_quandoDadosValidosComUser() throws Exception {
        Pedido novoPedido = new Pedido(null, new Date(), clienteValido, List.of(joiaValida));
        String pedidoJson = objectMapper.writeValueAsString(novoPedido);
        when(pedidoService.save(any(Pedido.class))).thenReturn("Pedido salvo com sucesso!");

        mockMvc.perform(post("/api/pedidos/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Pedido salvo com sucesso!"));
        verify(pedidoService, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve criar pedido com sucesso com ROLE_ADMIN")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void save_deveCriarPedido_quandoDadosValidosComAdmin() throws Exception {
        Pedido novoPedido = new Pedido(null, new Date(), clienteValido, List.of(joiaValida));
        String pedidoJson = objectMapper.writeValueAsString(novoPedido);
        when(pedidoService.save(any(Pedido.class))).thenReturn("Pedido salvo com sucesso!");

        mockMvc.perform(post("/api/pedidos/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Pedido salvo com sucesso!"));
        verify(pedidoService, times(1)).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve retornar Bad Request para pedido inválido (data nula)")
    @WithMockUser(username = "testuser")
    void save_deveRetornarBadRequest_quandoDataPedidoNula() throws Exception {
        Pedido pedidoInvalido = new Pedido(null, null, clienteValido, List.of(joiaValida));
        String pedidoJson = objectMapper.writeValueAsString(pedidoInvalido);

        mockMvc.perform(post("/api/pedidos/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dataPedido").value("A data do pedido não pode ser nula."));
        verify(pedidoService, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findById/{id} - Deve retornar pedido existente com ROLE_USER")
    @WithMockUser(username = "testuser")
    void findById_deveRetornarPedido_quandoIdExisteComUser() throws Exception {
        Long idExistente = 1L;
        when(pedidoService.findById(idExistente)).thenReturn(pedidoValido1);

        mockMvc.perform(get("/api/pedidos/findById/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(idExistente.intValue())))
                .andExpect(jsonPath("$.dataPedido", is(dateFormat.format(pedidoValido1.getDataPedido()))))
                .andExpect(jsonPath("$.cliente.nome", is(clienteValido.getNome())));
        verify(pedidoService, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findById/{id} - Deve retornar Bad Request se serviço lança RuntimeException")
    @WithMockUser(username = "testuser")
    void findById_deveRetornarBadRequest_quandoServicoLancaExcecao() throws Exception {
        Long idInexistente = 99L;
        String mensagemErro = "Pedido ID " + idInexistente + " não encontrado!";
        when(pedidoService.findById(idInexistente)).thenThrow(new RuntimeException(mensagemErro));

        mockMvc.perform(get("/api/pedidos/findById/{id}", idInexistente))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(mensagemErro));
        verify(pedidoService, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findAll - Deve retornar todos os pedidos com ROLE_ADMIN")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void findAll_deveRetornarTodosPedidos_comAdmin() throws Exception {
        List<Pedido> listaPedidos = List.of(pedidoValido1, pedidoValido2);
        when(pedidoService.findAll()).thenReturn(listaPedidos);

        mockMvc.perform(get("/api/pedidos/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(pedidoValido1.getId().intValue())));
        verify(pedidoService, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findAll - Deve retornar todos os pedidos com ROLE_USER")
    @WithMockUser(username = "testuser")
    void findAll_deveRetornarTodosPedidos_comUser() throws Exception {
        List<Pedido> listaPedidos = List.of(pedidoValido1, pedidoValido2);
        when(pedidoService.findAll()).thenReturn(listaPedidos);

        mockMvc.perform(get("/api/pedidos/findAll"))
                .andExpect(status().isOk()) // Deve ser Ok
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(pedidoValido1.getId().intValue())));
        verify(pedidoService, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findByClienteNomeContaining - Deve retornar pedidos com ROLE_ADMIN")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void findByClienteNomeContaining_deveRetornarPedidos_comAdmin() throws Exception {
        String nomeCliente = "Mock";
        when(pedidoService.findByClienteNomeContaining(nomeCliente)).thenReturn(List.of(pedidoValido1));

        mockMvc.perform(get("/api/pedidos/findByClienteNomeContaining").param("nomeCliente", nomeCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cliente.nome", is(clienteValido.getNome())));
        verify(pedidoService).findByClienteNomeContaining(nomeCliente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findByClienteNomeContaining - Deve retornar pedidos com ROLE_USER")
    @WithMockUser(username = "testuser")
    void findByClienteNomeContaining_deveRetornarPedidos_comUser() throws Exception {
        String nomeCliente = "Mock";
        when(pedidoService.findByClienteNomeContaining(nomeCliente)).thenReturn(List.of(pedidoValido1));

        mockMvc.perform(get("/api/pedidos/findByClienteNomeContaining").param("nomeCliente", nomeCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cliente.nome", is(clienteValido.getNome())));
        verify(pedidoService).findByClienteNomeContaining(nomeCliente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findByDataPedido - Deve retornar pedidos com ROLE_USER")
    @WithMockUser(username = "testuser")
    void findByDataPedido_deveRetornarPedidos_comUser() throws Exception {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dataParam = isoDateFormat.format(pedidoValido1.getDataPedido());

        when(pedidoService.findByDataPedido(any(Date.class))).thenReturn(List.of(pedidoValido1));

        mockMvc.perform(get("/api/pedidos/findByDataPedido").param("dataPedido", dataParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(pedidoValido1.getId().intValue())));
        verify(pedidoService).findByDataPedido(any(Date.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findByDataPedidoBetween - Deve retornar pedidos com ROLE_ADMIN")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void findByDataPedidoBetween_deveRetornarPedidos_comAdmin() throws Exception {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dataInicio = pedidoValido2.getDataPedido();
        Date dataFim = pedidoValido1.getDataPedido();

        String startDateParam = isoDateFormat.format(dataInicio);
        String endDateParam = isoDateFormat.format(dataFim);

        when(pedidoService.findByDataPedidoBetween(any(Date.class), any(Date.class))).thenReturn(List.of(pedidoValido1, pedidoValido2));

        mockMvc.perform(get("/api/pedidos/findByDataPedidoBetween")
                        .param("startDate", startDateParam)
                        .param("endDate", endDateParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        verify(pedidoService).findByDataPedidoBetween(any(Date.class), any(Date.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - PUT /update/{id} - Deve atualizar pedido com sucesso com ROLE_ADMIN")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void update_deveAtualizarPedido_quandoDadosValidosComAdmin() throws Exception {
        Long idParaAtualizar = 1L;
        String pedidoJson = objectMapper.writeValueAsString(pedidoValido1);
        String mensagemSucesso = "Pedido ID " + idParaAtualizar + " atualizado com sucesso!";
        when(pedidoService.update(any(Pedido.class), eq(idParaAtualizar))).thenReturn(mensagemSucesso);

        mockMvc.perform(put("/api/pedidos/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));
        verify(pedidoService, times(1)).update(any(Pedido.class), eq(idParaAtualizar));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - PUT /update/{id} - Deve retornar Bad Request para dados inválidos")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void update_deveRetornarBadRequest_quandoDadosInvalidos() throws Exception {
        Long idParaAtualizar = 1L;
        Pedido pedidoInvalido = new Pedido(idParaAtualizar, null, clienteValido, List.of(joiaValida));
        String pedidoJson = objectMapper.writeValueAsString(pedidoInvalido);

        mockMvc.perform(put("/api/pedidos/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dataPedido").value("A data do pedido não pode ser nula."));
        verify(pedidoService, never()).update(any(), anyLong());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - PUT /update/{id} - Deve retornar Bad Request (Access Denied) para ROLE_USER")
    @WithMockUser(username = "testuser")
    void update_deveRetornarBadRequestAccessDenied_comUser() throws Exception {
        Long idParaAtualizar = 1L;
        String pedidoJson = objectMapper.writeValueAsString(pedidoValido1);

        mockMvc.perform(put("/api/pedidos/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Access Denied")));
        verify(pedidoService, never()).update(any(Pedido.class), anyLong());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - DELETE /delete/{id} - Deve deletar pedido com sucesso com ROLE_ADMIN")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void delete_deveDeletarPedido_comAdmin() throws Exception {
        Long idParaDeletar = 1L;
        String mensagemSucesso = "Pedido ID " + idParaDeletar + " deletado com sucesso!";
        when(pedidoService.delete(idParaDeletar)).thenReturn(mensagemSucesso);

        mockMvc.perform(delete("/api/pedidos/delete/{id}", idParaDeletar))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));
        verify(pedidoService, times(1)).delete(idParaDeletar);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - DELETE /delete/{id} - Deve retornar Bad Request (Access Denied) para ROLE_USER")
    @WithMockUser(username = "testuser")
    void delete_deveRetornarBadRequestAccessDenied_comUser() throws Exception {
        Long idParaDeletar = 1L;
        mockMvc.perform(delete("/api/pedidos/delete/{id}", idParaDeletar))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Access Denied")));
        verify(pedidoService, never()).delete(idParaDeletar);
    }
}