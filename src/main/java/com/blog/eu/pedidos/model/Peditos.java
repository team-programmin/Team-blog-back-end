package com.blog.eu.pedidos.model;

import java.time.LocalDateTime;

import com.blog.eu.enuns.StatusPedido;
import com.blog.eu.enuns.TiposPeditos;
import com.blog.eu.model.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "peditos")
public class Peditos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonManagedReference
    private User userQuePediu;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;
    
    private LocalDateTime tempoQuePediu;
    @Enumerated(EnumType.STRING)
    private TiposPeditos tipo;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private Integer posicaoFila;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUserQuePediu() {
        return userQuePediu;
    }
    public void setUserQuePediu(User userQuePediu) {
        this.userQuePediu = userQuePediu;
    }
    public User getAdmin() {
        return admin;
    }
    public void setAdmin(User admin) {
        this.admin = admin;
    }
    public TiposPeditos getTipo() {
        return tipo;
    }
    public void setTipo(TiposPeditos tipo) {
        this.tipo = tipo;
    }
    public LocalDateTime getTempoQuePediu() {
        return tempoQuePediu;
    }
    public void setTempoQuePediu(LocalDateTime tempoQuePediu) {
        this.tempoQuePediu = tempoQuePediu;
    }
    public StatusPedido getStatus() {
        return status;
    }
    public void setStatus(StatusPedido status) {
        this.status = status;
    }
    public Integer getPosicaoFila() {
        return posicaoFila;
    }
    public void setPosicaoFila(Integer posicaoFila) {
        this.posicaoFila = posicaoFila;
    }
    
}
