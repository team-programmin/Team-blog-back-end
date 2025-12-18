package com.blog.eu.pedidos.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blog.eu.enuns.StatusPedido;
import com.blog.eu.pedidos.model.Peditos;

public interface PeditosC extends JpaRepository<Peditos, Long> {
    List<Peditos> findByStatusOrderByPosicaoFilaAsc(StatusPedido status);
    @Query("SELECT COALESCE(MAX(p.posicaoFila), 0) FROM Peditos p WHERE p.status = 'NA_FILA'")
    Integer findMaxPosicaoFila();
    

}
