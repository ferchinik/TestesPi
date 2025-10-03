package com.joia.config;

import com.joia.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private String secretKey = "TesteSecretoMuitoLongoParaSimularUmaChaveRealComMaisDeCinquentaCaracteresSegurosParaTestesUnitarios";
    private int jwtExpirationMs = 3600000; // 1 hora para testes
    private Key signingKeyObject;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
        signingKeyObject = Keys.hmacShaKeyFor(secretKey.getBytes());
        ReflectionTestUtils.invokeMethod(jwtUtils, "key");
    }

    private String generateTestToken(String username, String nomeCompleto, Long id, Set<String> roles, Date issuedAt, Date expirationDate, Key keyToSignWith) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("nomeCompleto", nomeCompleto)
                .claim("roles", roles)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(keyToSignWith, SignatureAlgorithm.HS512)
                .compact();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve gerar token JWT com sucesso")
    void generateJwtToken_deveGerarTokenValido() {
        Authentication authentication = mock(Authentication.class);
        User userPrincipal = new User("testuser", "password", "Test User Full Name", Set.of("ROLE_USER", "ROLE_VIEWER"));
        userPrincipal.setId(1L);

        // Mocking UserDetails part of the User principal
        Collection<GrantedAuthority> authorities = userPrincipal.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // User implements UserDetails, so getAuthorities() is available on userPrincipal
        // No need to separately mock authentication.getAuthorities() if UserDetails is the principal

        when(authentication.getPrincipal()).thenReturn(userPrincipal);

        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotNull().isNotEmpty();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKeyObject).build().parseClaimsJws(token).getBody();
        assertThat(claims.getSubject()).isEqualTo("testuser");
        assertThat(claims.get("id", Long.class)).isEqualTo(1L);
        assertThat(claims.get("nomeCompleto", String.class)).isEqualTo("Test User Full Name");

        @SuppressWarnings("unchecked")
        List<String> rolesFromToken = claims.get("roles", List.class);
        assertThat(rolesFromToken).containsExactlyInAnyOrderElementsOf(userPrincipal.getRoles());

        Date expiration = claims.getExpiration();
        assertThat(claims.getIssuedAt().getTime()).isLessThanOrEqualTo(new Date().getTime());
        assertThat(expiration.getTime()).isCloseTo(claims.getIssuedAt().getTime() + jwtExpirationMs, within(2000L)); // Increased tolerance slightly
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve extrair username de um token válido")
    void getUsernameFromJwtToken_deveRetornarUsername() {
        Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        String token = generateTestToken("testuser", "Nome Completo", 1L, Set.of("ROLE_USER"), now, expiryDate, signingKeyObject);

        String username = jwtUtils.getUsernameFromJwtToken(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve validar um token JWT corretamente")
    void validateJwtToken_deveRetornarTrue_paraTokenValido() {
        Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        String token = generateTestToken("validuser", "Nome", 2L, Set.of("ROLE_TEST"), now, expiryDate, signingKeyObject);

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve invalidar um token JWT expirado")
    void validateJwtToken_deveRetornarFalse_paraTokenExpirado() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 2000);
        Date issuedAt = new Date(now.getTime() - jwtExpirationMs - 3000);
        String expiredToken = generateTestToken("expireduser", "Nome", 3L, Set.of("ROLE_USER"), issuedAt, expiryDate, signingKeyObject);

        boolean isValid = jwtUtils.validateJwtToken(expiredToken);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve invalidar um token JWT com assinatura incorreta")
    void validateJwtToken_deveRetornarFalse_paraAssinaturaInvalida() {
        Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        String token = generateTestToken("userassinatura", "Nome", 4L, Set.of("ROLE_USER"), now, expiryDate, signingKeyObject);
        String tokenAdulterado = token.substring(0, token.length() - 5) + "XXXXX";

        boolean isValidAdulterado = jwtUtils.validateJwtToken(tokenAdulterado);
        assertFalse(isValidAdulterado, "Token adulterado deveria ser inválido");
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve invalidar um token que é estruturalmente inválido (e.g. não 3 partes)")
    void validateJwtToken_deveRetornarFalse_paraTokenEstruturalmenteInvalido() {
        // This token is missing parts and will cause MalformedJwtException
        String structurallyInvalidToken = "invalid";
        boolean isValid = jwtUtils.validateJwtToken(structurallyInvalidToken);
        assertFalse(isValid, "Token estruturalmente inválido deveria retornar false.");
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve invalidar um token JWT que causa UnsupportedJwtException (ex: alg 'none' when key is set)")
    void validateJwtToken_deveRetornarFalse_paraTokenComAlgNoneQuandoChaveDefinida() {
        // This token represents an unsecured JWT (alg:none)
        String algNoneToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ0ZXN0LXVuc3VwcG9ydGVkIn0.";
        // When JwtUtils.validateJwtToken calls parse with a signingKey set,
        // JJWT should throw UnsupportedJwtException for "alg":"none".
        // Your validateJwtToken method should catch this and return false.
        boolean isValid = jwtUtils.validateJwtToken(algNoneToken);
        assertFalse(isValid, "Token com alg=none deveria ser inválido quando uma chave de assinatura é esperada no parser.");
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve invalidar um token JWT com payload vazio (IllegalArgumentException via lib)")
    void validateJwtToken_deveRetornarFalse_paraPayloadVazioDetectadoPelaLib() {
        String emptyPayloadToken = "";
        boolean isValid = jwtUtils.validateJwtToken(emptyPayloadToken);
        assertFalse(isValid);
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve parsear JWT do header Authorization corretamente")
    void parseJwt_deveRetornarToken_quandoHeaderValido() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer test.token.string.jwt");

        String token = jwtUtils.parseJwt(request);
        assertThat(token).isEqualTo("test.token.string.jwt");
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve retornar null ao parsear JWT se header ausente")
    void parseJwt_deveRetornarNull_quandoHeaderAusente() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(null);
        String token = jwtUtils.parseJwt(request);
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve retornar null ao parsear JWT se header não começa com Bearer")
    void parseJwt_deveRetornarNull_quandoHeaderNaoComecaComBearer() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("NotBearer test.token.string");
        String token = jwtUtils.parseJwt(request);
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve retornar string vazia ao parsear JWT se header é apenas 'Bearer ' (com espaço)")
    void parseJwt_deveRetornarStringVazia_quandoHeaderApenasBearerComEspaco() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        String token = jwtUtils.parseJwt(request);
        assertThat(token).isEqualTo("");
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve retornar null ao parsear JWT se header é apenas 'Bearer' (sem espaço e sem token)")
    void parseJwt_deveRetornarNull_quandoHeaderApenasBearerSemEspacoOuToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer");
        String token = jwtUtils.parseJwt(request);
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("TESTE DE UNIDADE - Deve retornar todas as claims de um token válido")
    void getAllClaimsFromToken_deveRetornarClaims() {
        Date now = new Date(System.currentTimeMillis() / 1000 * 1000);
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String username = "claimuser";
        Long id = 5L;
        String nomeCompleto = "Claim User Full";
        Set<String> roles = Set.of("ROLE_CLAIM_TESTER");
        String token = generateTestToken(username, nomeCompleto, id, roles, now, expiryDate, signingKeyObject);

        Claims claims = jwtUtils.getAllClaimsFromToken(token);

        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("id", Long.class)).isEqualTo(id);
        assertThat(claims.get("nomeCompleto", String.class)).isEqualTo(nomeCompleto);

        @SuppressWarnings("unchecked")
        List<String> rolesFromClaims = claims.get("roles", List.class);
        assertThat(rolesFromClaims).containsExactlyInAnyOrderElementsOf(roles);

        assertThat(claims.getIssuedAt().getTime()).isCloseTo(now.getTime(), within(1000L));
        assertThat(claims.getExpiration().getTime()).isCloseTo(expiryDate.getTime(), within(1000L));
    }
}