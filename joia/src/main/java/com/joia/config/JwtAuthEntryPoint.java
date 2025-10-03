package com.joia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Componente que trata as exceções de autenticação (AuthenticationException).
 * É acionado quando um usuário não autenticado tenta acessar um recurso protegido.
 * Formata a resposta de erro como JSON.
 */
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    // ObjectMapper para serializar o corpo da resposta para JSON.
    // É thread-safe e pode ser reutilizado.
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Este método é chamado sempre que uma exceção é lançada devido a um principal
     * não autenticado tentando acessar um recurso que requer autenticação.
     *
     * @param request       que resultou em uma AuthenticationException
     * @param response      para que possamos modificar para enviar uma resposta de erro
     * @param authException que causou a invocação
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Loga o erro de autenticação no servidor.
        logger.error("Erro de não autorizado: {}", authException.getMessage());

        // Define o tipo de conteúdo da resposta como JSON.
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Define o status da resposta HTTP como 401 Unauthorized.
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Cria o corpo da resposta JSON.
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis()); // Adiciona um timestamp para o erro
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Não Autorizado");
        body.put("message", authException.getMessage()); // A mensagem da exceção original (ex: "Bad credentials")
        body.put("path", request.getServletPath()); // O caminho da requisição que falhou

        // Escreve o mapa 'body' como uma string JSON no output stream da resposta.
        // Isso garante que o frontend receba um JSON de erro.
        try {
            objectMapper.writeValue(response.getOutputStream(), body);
        } catch (IOException e) {
            // Se houver um erro ao escrever a resposta JSON (raro, mas possível)
            logger.error("Erro ao escrever a resposta JSON de erro: {}", e.getMessage());
            // Como fallback, envia um erro HTTP genérico se não conseguir escrever o JSON
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro de autorização, falha ao formatar resposta JSON.");
        }
    }
}
