package com.blog.eu.infos;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * Componente responsável por contabilizar o número de requisições realizadas por dia.
 *
 * O contador é reiniciado automaticamente quando o dia muda, garantindo que os valores
 * sejam sempre referentes ao dia atual. Utiliza {@link AtomicInteger} para garantir
 * segurança em ambientes concorrentes.
 *
 * Funcionalidades principais:
 * - Incrementar o contador de requisições
 * - Obter o número atual de requisições realizadas no dia
 *
 * Este componente pode ser injetado em controladores ou serviços para monitorar
 * estatísticas de uso da aplicação.
 *
 * @author Luis
 */
@Component
public class RequestCounter {

    /** Contador de requisições do dia atual */
    private AtomicInteger counter = new AtomicInteger(0);

    /** Dia atual utilizado para validar e reiniciar o contador */
    private LocalDate currentDay = LocalDate.now();

    /**
     * Incrementa o contador de requisições.
     * Caso o dia tenha mudado, o contador é reiniciado antes de incrementar.
     */
    public void increment() {
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDay)) {
            counter.set(0);
            currentDay = today;
        }
        counter.incrementAndGet();
    }

    /**
     * Obtém o número de requisições realizadas no dia atual.
     * Caso o dia tenha mudado, o contador é reiniciado antes de retornar o valor.
     *
     * @return número de requisições realizadas no dia
     */
    public int getCount() {
        LocalDate today = LocalDate.now();
        if (!today.equals(currentDay)) {
            counter.set(0);
            currentDay = today;
        }
        return counter.get();
    }
}
