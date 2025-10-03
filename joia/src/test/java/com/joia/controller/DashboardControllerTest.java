package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joia.dto.FaturamentoDTO;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /api/dashboard/faturamento - Deve retornar faturamento para ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getFaturamentoTotal_quandoUsuarioAdmin_deveRetornarFaturamentoDTO() throws Exception {
        // Arrange
        FaturamentoDTO mockFaturamento = new FaturamentoDTO(new BigDecimal("5780.75"));
        when(pedidoService.calcularFaturamentoTotal()).thenReturn(mockFaturamento);

        // Act & Assert
        mockMvc.perform(get("/api/dashboard/faturamento")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorTotal", is(5780.75)));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /api/dashboard/faturamento - Deve retornar 400 (Access Denied) para USER") // Nome do teste atualizado
    @WithMockUser(username = "testuser")
    void getFaturamentoTotal_quandoUsuarioUser_deveRetornarBadRequestComAccessDenied() throws Exception { // Nome do método atualizado
        // Act & Assert
        mockMvc.perform(get("/api/dashboard/faturamento")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Access Denied")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - GET /api/dashboard/faturamento - Deve retornar 401 para Não Autenticado")
    void getFaturamentoTotal_quandoNaoAutenticado_deveRetornarUnauthorized() throws Exception {
         // Act & Assert
        mockMvc.perform(get("/api/dashboard/faturamento")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}