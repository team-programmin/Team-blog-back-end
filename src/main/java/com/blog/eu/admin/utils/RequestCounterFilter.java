package com.blog.eu.admin.utils;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.blog.eu.infos.RequestCounter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro responsável por contabilizar requisições recebidas pela aplicação.
 *
 * Este filtro é executado uma vez por requisição (extends {@link OncePerRequestFilter})
 * e incrementa o contador mantido pelo componente {@link RequestCounter}.
 * Após a contagem, a requisição continua normalmente na cadeia de filtros.
 *
 * Funcionalidade principal:
 * - Incrementar o número de requisições registradas no sistema
 *
 * Esse filtro é útil para monitoramento e estatísticas de uso da aplicação.
 *
 * @author Luis
 * @see RequestCounter
 */
@Component
public class RequestCounterFilter extends OncePerRequestFilter {

    private final RequestCounter requestCounter;

    /**
     * Construtor que injeta o componente {@link RequestCounter}.
     *
     * @param requestCounter componente responsável por manter o contador de requisições
     */
    public RequestCounterFilter(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    /**
     * Método chamado internamente para processar cada requisição HTTP.
     *
     * Incrementa o contador de requisições e continua a execução da cadeia de filtros.
     *
     * @param request  objeto {@link HttpServletRequest} representando a requisição
     * @param response objeto {@link HttpServletResponse} representando a resposta
     * @param filterChain cadeia de filtros a ser continuada após a contagem
     * @throws ServletException se ocorrer erro durante o processamento do filtro
     * @throws IOException se ocorrer erro de entrada/saída durante o processamento
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        requestCounter.increment();
        filterChain.doFilter(request, response);
    }
}
