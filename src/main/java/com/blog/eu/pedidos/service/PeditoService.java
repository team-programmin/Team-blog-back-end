package com.blog.eu.pedidos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import com.blog.eu.dto.DtoPedito;
import com.blog.eu.enuns.StatusPedido;
import com.blog.eu.model.User;
import com.blog.eu.pedidos.model.Peditos;
import com.blog.eu.pedidos.repository.PeditosC;

import jakarta.transaction.Transactional;

@Service
public class PeditoService {
    private final PeditosC peditosC;
   

    public PeditoService(PeditosC peditosC) {
        this.peditosC = peditosC;
        
    }

    @Transactional
    public List<Peditos> listarmeusPeditos(User user) {
        List<Peditos> peditos = peditosC.findAll();
        List<Peditos> peditos2 = new ArrayList<>();
        for(Peditos p : peditos){
            if(p.getUserQuePediu().getId().equals(user.getId())){
                peditos2.add(p);
            }
        }
        return peditos2;
    }
    @Transactional
    public Peditos add(DtoPedito dtoPedito, User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("O campo 'id' não pode ser nulo");
        }

       
        
        Peditos pedito = new Peditos();
        pedito.setUserQuePediu(user);
        pedito.setTempoQuePediu(LocalDateTime.now());
        pedito.setStatus(StatusPedido.NA_FILA);

        Integer maxPosicao = peditosC.findMaxPosicaoFila();
        if (maxPosicao == null) {
            maxPosicao = 0;
        }
        pedito.setPosicaoFila(maxPosicao + 1);

        return peditosC.save(pedito);
    }

    @Transactional
    public Peditos proximo() {
        List<Peditos> fila = peditosC.findByStatusOrderByPosicaoFilaAsc(com.blog.eu.enuns.StatusPedido.NA_FILA);
        if (fila.isEmpty()) {
            return null;
        }
        Peditos proximo = fila.get(0);
        proximo.setStatus(com.blog.eu.enuns.StatusPedido.PROCESSANDO);
        peditosC.save(proximo);
        
        for (int i = 1; i < fila.size(); i++) {
            Peditos p = fila.get(i);
            p.setPosicaoFila(p.getPosicaoFila() - 1);
            peditosC.save(p);
        }
        return proximo;
    }

    public List<Peditos> listarFila() {
        return peditosC.findByStatusOrderByPosicaoFilaAsc(StatusPedido.NA_FILA);
    }

    @Transactional
public Peditos finalizarEExcluir(Long pedidoId) {

    Peditos pedido = peditosC.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

    if (pedido.getStatus() != StatusPedido.NA_FILA) {
        throw new RuntimeException("Pedido não está na fila");
    }

    // Remover referência do usuário para evitar TransientPropertyValueException
    User user = pedido.getUserQuePediu();
    if (user != null) {
        user.setPedito(null); // Se User tiver campo pedito
    }

    int posicaoRemovida = pedido.getPosicaoFila();

    peditosC.delete(pedido);

    List<Peditos> restantes = peditosC.findByStatusOrderByPosicaoFilaAsc(StatusPedido.NA_FILA);

    for (Peditos p : restantes) {
        if (p.getPosicaoFila() > posicaoRemovida) {
            p.setPosicaoFila(p.getPosicaoFila() - 1);
            peditosC.save(p);
        }
    }
    return pedido;
}
public List<Peditos> listarPorStatus(StatusPedido status) {
    return peditosC.findByStatusOrderByPosicaoFilaAsc(status);
}

}
