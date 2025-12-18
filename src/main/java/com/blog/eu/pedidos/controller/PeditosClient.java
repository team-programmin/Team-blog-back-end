package com.blog.eu.pedidos.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blog.eu.dto.DtoPedito;
import com.blog.eu.dto.PedidoResumoDTO;
import com.blog.eu.enuns.StatusPedido;
import com.blog.eu.model.User;
import com.blog.eu.pedidos.model.Peditos;
import com.blog.eu.pedidos.repository.PeditosC;
import com.blog.eu.pedidos.service.PeditoService;
import com.blog.eu.service.EmailService;

@RestController
@RequestMapping("/api/peditos")
public class PeditosClient {
    private final EmailService  emailService;   
    private final PeditoService peditoService;
   
    private final PeditosC peditosC;
    private final String oie = "<!DOCTYPE html>\n" + //
                "<html lang=\"pt-BR\">\n" + //
                "<body style=\"margin:0;padding:0;background-color:#f4f6f8;font-family:Arial,Helvetica,sans-serif;color:#333;\">\n" + //
                "\n" + //
                "  <div style=\"max-width:600px;margin:40px auto;background:#ffffff;border-radius:12px;overflow:hidden;\">\n" + //
                "\n" + //
                "    <div style=\"background:#1e7aff;color:#fff;text-align:center;padding:24px;\">\n" + //
                "      <h1 style=\"margin:0;font-size:22px;\">✅ Pedido Confirmado</h1>\n" + //
                "    </div>\n" + //
                "\n" + //
                "    <div style=\"padding:24px;text-align:center;\">\n" + //
                "      <p style=\"font-size:16px;line-height:1.6;margin:0 0 16px;\">Olá,</p>\n" + //
                "      <p style=\"font-size:16px;line-height:1.6;margin:0 0 16px;\">\n" + //
                "        Recebemos seu pedido de produto na <strong>Team</strong> com sucesso.\n" + //
                "      </p>\n" + //
                "      <p style=\"font-size:16px;line-height:1.6;margin:0 0 24px;\">\n" + //
                "        Em breve você receberá atualizações sobre o andamento.\n" + //
                "      </p>\n" + //
                "\n" + //
                "      <a\n" + //
                "        href=\"https://team.example.com/meus-pedidos\"\n" + //
                "        style=\"display:inline-block;background:#1e7aff;color:#ffffff;\n" + //
                "               padding:12px 20px;border-radius:6px;\n" + //
                "               text-decoration:none;font-weight:bold;\">\n" + //
                "        Acompanhar Pedido\n" + //
                "      </a>\n" + //
                "    </div>\n" + //
                "\n" + //
                "    <div style=\"background:#f4f6f8;text-align:center;padding:16px;\n" + //
                "                font-size:12px;color:#777;\">\n" + //
                "      Esta é uma mensagem automática. Não responda este e-mail.<br>\n" + //
                "      © 2025 Team\n" + //
                "    </div>\n" + //
                "\n" + //
                "  </div>\n" + //
                "\n" + //
                "</body>\n" + //
                "</html>\n" + //
                "";

     public PeditosClient(PeditosC peditosC, PeditoService peditoService, EmailService emailService) {
        this.peditosC = peditosC;
        this.peditoService = peditoService;
        this.emailService = emailService;
    }

    @GetMapping("/position")
    public ResponseEntity<Integer> getMyPosition(@AuthenticationPrincipal User user) {
        if (user.getPedito() == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Peditos> fila = peditosC.findByStatusOrderByPosicaoFilaAsc(StatusPedido.NA_FILA);
        for (int i = 0; i < fila.size(); i++) {
            if (fila.get(i).getUserQuePediu().getId().equals(user.getId())) {
                return ResponseEntity.ok(i + 1);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public List<Peditos> getAllPeditos() {
        return peditoService.listarFila();
    }

    @GetMapping("/next")
    @PreAuthorize("hasRole('ADMIN')")
    public Peditos getNextPedito() {
        return peditoService.proximo();
    }

    // DTO simples só com id e status
    public static class PedidoFinalizadoDTO {
        private Long id;
        private String status;

        public PedidoFinalizadoDTO(Long id, String status) {
            this.id = id;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public String getStatus() {
            return status;
        }
    }
    public static class PedidoIdDTO {
    private Long id;

    public PedidoIdDTO() {} // Jackson precisa do construtor vazio

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
    @GetMapping("/atendimento")
    public ResponseEntity<List<PedidoResumoDTO>> getPedidosEmAtendimento() {
    List<Peditos> pedidos = peditoService.listarPorStatus(StatusPedido.PROCESSANDO);
    List<PedidoResumoDTO> resposta = pedidos.stream()
        .map(p -> new PedidoResumoDTO(
            p.getId(),
            p.getUserQuePediu().getDisplayName(),
            p.getTempoQuePediu(),
            p.getPosicaoFila(),
            p.getStatus()
        ))
        .toList();
    return ResponseEntity.ok(resposta);
}

    @PutMapping("/finnality")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoFinalizadoDTO> finnality(@RequestBody PedidoIdDTO pedito) {
        Peditos finalizado = peditoService.finalizarEExcluir(pedito.getId());
        PedidoFinalizadoDTO resposta = new PedidoFinalizadoDTO(finalizado.getId(), finalizado.getStatus().name());
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/add")
    public ResponseEntity<Peditos> add(@RequestBody DtoPedito pedito, @AuthenticationPrincipal User user) {
        emailService.enviarHtml(user.getEmail(), "Você pediu um produto na team", oie);
        peditoService.add(pedito, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PedidoResumoDTO>> getMyOrders(@AuthenticationPrincipal User user) {
        List<Peditos> pedidos = peditoService.listarmeusPeditos(user);
        List<PedidoResumoDTO> resposta = pedidos.stream()
            .map(p -> new PedidoResumoDTO(
                p.getId(),
                user.getDisplayName(),
                p.getTempoQuePediu(),
                p.getPosicaoFila(),
                p.getStatus()
            ))
            .toList();
        return ResponseEntity.ok(resposta);
    }
}
