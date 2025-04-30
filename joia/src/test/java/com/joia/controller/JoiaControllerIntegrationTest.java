package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joia.entity.Categoria;
import com.joia.entity.Fornecedor;
import com.joia.entity.Joia;
import com.joia.service.JoiaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
class JoiaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JoiaService joiaServiceMock;

    @Test
    @DisplayName("[Integration Test com Mock] POST /save - Deve criar joia com sucesso")
    void save_deveCriarJoia_quandoDadosValidos() throws Exception {
        Categoria cat = new Categoria(1L, "Anel", null);
        Fornecedor forn = new Fornecedor(1L, "Ouro Nobre", null);
        Joia novaJoia = new Joia(null, "Anel Solitário", new BigDecimal("1500.00"), cat, forn, null);
        String joiaJson = objectMapper.writeValueAsString(novaJoia);
        when(joiaServiceMock.save(any(Joia.class))).thenReturn("Joia salva com sucesso!");

        mockMvc.perform(post("/api/joias/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(joiaJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Joia salva com sucesso!"));

        verify(joiaServiceMock, times(1)).save(any(Joia.class));
    }

    @Test
    @DisplayName("[Integration Test com Mock] GET /findAll - Deve retornar todas as joias")
    void findAll_deveRetornarTodasJoias() throws Exception {
        Categoria cat = new Categoria(1L, "Anel", null);
        Fornecedor forn = new Fornecedor(1L, "Ouro Nobre", null);
        Joia j1 = new Joia(1L, "Anel Solitário", new BigDecimal("1500.00"), cat, forn, new ArrayList<>());
        Joia j2 = new Joia(2L, "Brinco Ouro", new BigDecimal("500.00"), cat, forn, new ArrayList<>());
        when(joiaServiceMock.findAll()).thenReturn(List.of(j1, j2));

        mockMvc.perform(get("/api/joias/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].nome", is("Anel Solitário")))
                .andExpect(jsonPath("$[1].nome", is("Brinco Ouro")));

        verify(joiaServiceMock, times(1)).findAll();
    }
    @Test
    @DisplayName("[Integration Test com Mock] GET /findById/{id} - Deve retornar joia existente")
    void findById_deveRetornarJoia_quandoIdExiste() throws Exception {
        Long idExistente = 1L;
        Categoria cat = new Categoria(1L, "Anel", null);
        Fornecedor forn = new Fornecedor(1L, "Ouro Nobre", null);
        Joia joiaMock = new Joia(idExistente, "Anel Solitário", new BigDecimal("1500.00"), cat, forn, new ArrayList<>());
        when(joiaServiceMock.findById(idExistente)).thenReturn(joiaMock);

        mockMvc.perform(get("/api/joias/findById/{id}", idExistente))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(idExistente.intValue())))
                .andExpect(jsonPath("$.nome", is("Anel Solitário")));

        verify(joiaServiceMock, times(1)).findById(idExistente);
    }
    
    @Test
    @DisplayName("[Integration Test com Mock] PUT /update/{id} - Deve atualizar joia com sucesso")
    void update_deveAtualizarJoia_quandoDadosValidos() throws Exception {
        // Arrange
        Long idParaAtualizar = 1L;
        Categoria cat = new Categoria(1L, "Anel", null);
        Fornecedor forn = new Fornecedor(1L, "Ouro Nobre", null);
        Joia dadosUpdate = new Joia(idParaAtualizar, "Anel Solitário Atualizado", new BigDecimal("1600.00"), cat, forn, null);
        String joiaJson = objectMapper.writeValueAsString(dadosUpdate);
        String mensagemSucesso = "Joia ID " + idParaAtualizar + " atualizada com sucesso!";
        when(joiaServiceMock.update(any(Joia.class), eq(idParaAtualizar))).thenReturn(mensagemSucesso);

        // Act & Assert
        mockMvc.perform(put("/api/joias/update/{id}", idParaAtualizar)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(joiaJson))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(joiaServiceMock, times(1)).update(any(Joia.class), eq(idParaAtualizar));
    }

    // --- Teste para DELETE /api/joias/delete/{id} ---
    @Test
    @DisplayName("[Integration Test com Mock] DELETE /delete/{id} - Deve deletar joia com sucesso")
    void delete_deveDeletarJoia_quandoIdExiste() throws Exception {
        // Arrange
        Long idParaDeletar = 1L;
        String mensagemSucesso = "Joia ID " + idParaDeletar + " deletada com sucesso!";
        when(joiaServiceMock.delete(idParaDeletar)).thenReturn(mensagemSucesso);

        // Act & Assert
        mockMvc.perform(delete("/api/joias/delete/{id}", idParaDeletar))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(joiaServiceMock, times(1)).delete(idParaDeletar);
    }

    // Adicionar testes para os endpoints de filtro (findAllByOrderByNomeAsc, filtrar) se desejar
}
