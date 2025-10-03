package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joia.entity.Categoria;
import com.joia.service.CategoriaService;
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
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaServiceMock;

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve criar categoria com sucesso")
    @WithMockUser(username = "testadmin", roles= {"ADMIN"})
    void save_deveCriarCategoria_quandoDadosValidos() throws Exception {
        Categoria novaCategoria = new Categoria(null, "Brincos", null);
        String categoriaJson = objectMapper.writeValueAsString(novaCategoria);
        when(categoriaServiceMock.save(any(Categoria.class))).thenReturn("Categoria salva com sucesso!");

        mockMvc.perform(post("/api/categorias/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson))
                .andExpect(status().isCreated()) // Espera 201 Created
                .andExpect(content().string("Categoria salva com sucesso!"));

        verify(categoriaServiceMock, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /save - Deve retornar Bad Request com nome vazio")
    @WithMockUser(username = "testadmin", roles= {"ADMIN"})
    void save_deveRetornarBadRequest_quandoNomeVazio() throws Exception {
        Categoria categoriaInvalida = new Categoria(null, "", null);
        String categoriaJson = objectMapper.writeValueAsString(categoriaInvalida);

        mockMvc.perform(post("/api/categorias/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").value("O nome da categoria não pode ser vazio."));

        verify(categoriaServiceMock, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findAll - Deve retornar todas as categorias")
    @WithMockUser(username = "testuser")
    void findAll_deveRetornarTodasCategorias() throws Exception {
        Categoria cat1 = new Categoria(1L, "Anéis", new ArrayList<>());
        Categoria cat2 = new Categoria(2L, "Colares", new ArrayList<>());
        when(categoriaServiceMock.findAll()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/categorias/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nome", is("Anéis")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nome", is("Colares")));

        verify(categoriaServiceMock, times(1)).findAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findById/{id} - Deve retornar categoria existente")
    @WithMockUser(username = "testuser")
    void findById_deveRetornarCategoria_quandoIdExiste() throws Exception {
        Long idExistente = 1L;
        Categoria categoriaMock = new Categoria(idExistente, "Anéis", new ArrayList<>());
        when(categoriaServiceMock.findById(idExistente)).thenReturn(categoriaMock);

        mockMvc.perform(get("/api/categorias/findById/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(idExistente.intValue()))) // jsonPath retorna int para Long
                .andExpect(jsonPath("$.nome", is("Anéis")));

        verify(categoriaServiceMock, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /findById/{id} - Deve retornar Bad Request se ID não existe")
    @WithMockUser(username = "testuser")
    void findById_deveRetornarBadRequest_quandoIdNaoExiste() throws Exception {
        Long idInexistente = 99L;
        String mensagemErro = "Categoria ID " + idInexistente + " não encontrada!";
        when(categoriaServiceMock.findById(idInexistente)).thenThrow(new RuntimeException(mensagemErro));

        mockMvc.perform(get("/api/categorias/findById/{id}", idInexistente)
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(mensagemErro))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(categoriaServiceMock, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - PUT /update/{id} - Deve atualizar categoria com sucesso")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void update_deveAtualizarCategoria_quandoDadosValidos() throws Exception {
        Long idParaAtualizar = 1L;
        Categoria dadosUpdate = new Categoria(idParaAtualizar, "Anéis de Ouro", null);
        String categoriaJson = objectMapper.writeValueAsString(dadosUpdate);
        String mensagemSucesso = "Categoria ID " + idParaAtualizar + " atualizada com sucesso!";
        when(categoriaServiceMock.update(any(Categoria.class), eq(idParaAtualizar))).thenReturn(mensagemSucesso);

        mockMvc.perform(put("/api/categorias/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(categoriaServiceMock, times(1)).update(any(Categoria.class), eq(idParaAtualizar));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - DELETE /delete/{id} - Deve deletar categoria com sucesso")
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void delete_deveDeletarCategoria_quandoIdExiste() throws Exception {
        Long idParaDeletar = 1L;
        String mensagemSucesso = "Categoria ID " + idParaDeletar + " deletada com sucesso!";
        when(categoriaServiceMock.delete(idParaDeletar)).thenReturn(mensagemSucesso);

        mockMvc.perform(delete("/api/categorias/delete/{id}", idParaDeletar))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(categoriaServiceMock, times(1)).delete(idParaDeletar);
    }
}
