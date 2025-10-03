package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joia.entity.Fornecedor;
import com.joia.service.FornecedorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FornecedorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FornecedorService fornecedorServiceMock;

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve criar fornecedor com sucesso")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void save_deveCriarFornecedor_quandoDadosValidos() throws Exception {
        Fornecedor novoFornecedor = new Fornecedor(null, "Prata Fina", null);
        String fornecedorJson = objectMapper.writeValueAsString(novoFornecedor);
        when(fornecedorServiceMock.save(any(Fornecedor.class))).thenReturn("Fornecedor salvo com sucesso!");

        mockMvc.perform(post("/api/fornecedores/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Fornecedor salvo com sucesso!"));

        verify(fornecedorServiceMock, times(1)).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve retornar Bad Request (Access Denied) para ROLE_USER")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void save_deveRetornarBadRequestAccessDenied_comUser() throws Exception {
        Fornecedor novoFornecedor = new Fornecedor(null, "Prata Fina", null);
        String fornecedorJson = objectMapper.writeValueAsString(novoFornecedor);

        mockMvc.perform(post("/api/fornecedores/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isBadRequest()) // Ou isForbidden()
                .andExpect(jsonPath("$.message", containsString("Access Denied")));

        verify(fornecedorServiceMock, never()).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve retornar Bad Request com nome vazio")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void save_deveRetornarBadRequest_quandoNomeVazio() throws Exception {
        Fornecedor fornecedorInvalido = new Fornecedor(null, "", null);
        String fornecedorJson = objectMapper.writeValueAsString(fornecedorInvalido);

        mockMvc.perform(post("/api/fornecedores/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").value("Nome não pode ser vazio.")); // Ajuste a mensagem se for diferente

        verify(fornecedorServiceMock, never()).save(any(Fornecedor.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findById/{id} - Deve retornar fornecedor existente")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void findById_deveRetornarFornecedor_quandoIdExiste() throws Exception {
        // Arrange
        Long idExistente = 1L;
        Fornecedor fornecedorMock = new Fornecedor(idExistente, "Ouro Nobre", new ArrayList<>());
        when(fornecedorServiceMock.findById(idExistente)).thenReturn(fornecedorMock);

        // Act & Assert
        mockMvc.perform(get("/api/fornecedores/findById/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(idExistente.intValue())))
                .andExpect(jsonPath("$.nome", is("Ouro Nobre")));

        verify(fornecedorServiceMock, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findAll - Deve retornar todos os fornecedores")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void findAll_deveRetornarTodosFornecedores() throws Exception {
        Fornecedor f1 = new Fornecedor(1L, "Ouro Nobre", new ArrayList<>());
        Fornecedor f2 = new Fornecedor(2L, "Prata Fina", new ArrayList<>());
        when(fornecedorServiceMock.findAll()).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/api/fornecedores/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].nome", is("Ouro Nobre")))
                .andExpect(jsonPath("$[1].nome", is("Prata Fina")));

        verify(fornecedorServiceMock, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - PUT /update/{id} - Deve atualizar fornecedor com sucesso")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void update_deveAtualizarFornecedor_quandoDadosValidos() throws Exception {
        Long idParaAtualizar = 1L;
        Fornecedor dadosUpdate = new Fornecedor(idParaAtualizar, "Ouro Nobre Atualizado", null);
        String fornecedorJson = objectMapper.writeValueAsString(dadosUpdate);
        String mensagemSucesso = "Fornecedor ID " + idParaAtualizar + " atualizado com sucesso!";
        when(fornecedorServiceMock.update(any(Fornecedor.class), eq(idParaAtualizar))).thenReturn(mensagemSucesso);

        mockMvc.perform(put("/api/fornecedores/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(fornecedorServiceMock, times(1)).update(any(Fornecedor.class), eq(idParaAtualizar));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - PUT /update/{id} - Deve retornar Bad Request (Access Denied) para ROLE_USER")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void update_deveRetornarBadRequestAccessDenied_comUser() throws Exception {
        Long idParaAtualizar = 1L;
        Fornecedor dadosUpdate = new Fornecedor(idParaAtualizar, "Ouro Nobre Atualizado", null);
        String fornecedorJson = objectMapper.writeValueAsString(dadosUpdate);

        mockMvc.perform(put("/api/fornecedores/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fornecedorJson))
                .andExpect(status().isBadRequest()) // Ou isForbidden()
                .andExpect(jsonPath("$.message", containsString("Access Denied")));

        verify(fornecedorServiceMock, never()).update(any(Fornecedor.class), eq(idParaAtualizar));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - DELETE /delete/{id} - Deve deletar fornecedor com sucesso")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void delete_deveDeletarFornecedor_quandoIdExiste() throws Exception {
        Long idParaDeletar = 1L;
        String mensagemSucesso = "Fornecedor ID " + idParaDeletar + " deletado com sucesso!";
        when(fornecedorServiceMock.delete(idParaDeletar)).thenReturn(mensagemSucesso);

        mockMvc.perform(delete("/api/fornecedores/delete/{id}", idParaDeletar))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(fornecedorServiceMock, times(1)).delete(idParaDeletar);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - DELETE /delete/{id} - Deve retornar Bad Request (Access Denied) para ROLE_USER")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void delete_deveRetornarBadRequestAccessDenied_comUser() throws Exception {
        Long idParaDeletar = 1L;

        mockMvc.perform(delete("/api/fornecedores/delete/{id}", idParaDeletar))
                .andExpect(status().isBadRequest()) // Ou isForbidden()
                .andExpect(jsonPath("$.message", containsString("Access Denied")));

        verify(fornecedorServiceMock, never()).delete(idParaDeletar);
    }

}
