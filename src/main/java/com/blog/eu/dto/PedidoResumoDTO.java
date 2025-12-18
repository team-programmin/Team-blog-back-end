package com.blog.eu.dto;

import java.time.LocalDateTime;

import com.blog.eu.enuns.StatusPedido;


public class PedidoResumoDTO {
    private Long id;
    private String usuario;
    private LocalDateTime tempoQuePediu;
    private Integer posicaoFila;
    public Integer getPosicaoFila() {
        return posicaoFila;
    }

    public void setPosicaoFila(Integer posicaoFila) {
        this.posicaoFila = posicaoFila;
    }
    private StatusPedido status;

    public PedidoResumoDTO(Long id, String usuario, LocalDateTime tempoQuePediu, Integer posicaoFila,
            StatusPedido status) {
        this.id = id;
        this.usuario = usuario;
        this.tempoQuePediu = tempoQuePediu;
        this.posicaoFila = posicaoFila;
        this.status = status;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public PedidoResumoDTO(Long id, String usuario, LocalDateTime tempoQuePediu) {
        this.id = id;
        this.usuario = usuario;
        this.tempoQuePediu = tempoQuePediu;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getTempoQuePediu() {
        return tempoQuePediu;
    }
    public void setTempoQuePediu(LocalDateTime tempoQuePediu) {
        this.tempoQuePediu = tempoQuePediu;
    }
}
