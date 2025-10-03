package com.joia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joia.config.JwtUtils;
import com.joia.dto.LoginRequest;
import com.joia.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManagerMock;

    @MockBean
    private JwtUtils jwtUtilsMock;

    private User mockUserEntity;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        mockUserEntity = new User("testuser", "encodedPassword", "Test User Full Name", Set.of("ROLE_USER"));
        mockUserEntity.setId(1L);

        mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserEntity);

        List<SimpleGrantedAuthority> authorities = mockUserEntity.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        doReturn(authorities).when(mockAuthentication).getAuthorities();
    }


    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /api/auth/login - Deve autenticar com sucesso e retornar JWT")
    void authenticateUser_deveRetornarJwt_quandoCredenciaisValidas() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        when(authenticationManagerMock.authenticate(
                any(UsernamePasswordAuthenticationToken.class))
        ).thenReturn(mockAuthentication);

        String fakeJwt = "fake.jwt.token.stringvalue";
        when(jwtUtilsMock.generateJwtToken(mockAuthentication)).thenReturn(fakeJwt);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", is(fakeJwt)))
                .andExpect(jsonPath("$.type", is("Bearer")))
                .andExpect(jsonPath("$.id", is(mockUserEntity.getId().intValue())))
                .andExpect(jsonPath("$.username", is(mockUserEntity.getUsername())))
                .andExpect(jsonPath("$.nomeCompleto", is(mockUserEntity.getNomeCompleto())))
                .andExpect(jsonPath("$.roles[0]", is("ROLE_USER")));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /api/auth/login - Deve retornar Bad Request para credenciais inválidas")
    void authenticateUser_deveRetornarBadRequest_quandoCredenciaisInvalidas() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpassword");

        String errorMessage = "Credenciais inválidas simuladas";
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException(errorMessage));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /api/auth/login - Deve retornar Bad Request para username vazio")
    void authenticateUser_deveRetornarBadRequest_quandoUsernameVazio() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.username").value("must not be blank"));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO - POST /api/auth/login - Deve retornar Bad Request para password vazio")
    void authenticateUser_deveRetornarBadRequest_quandoPasswordVazio() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("must not be blank"));
    }
}