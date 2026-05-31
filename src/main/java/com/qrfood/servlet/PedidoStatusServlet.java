package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.PedidoDAO;
import com.qrfood.model.StatusPedido;

/**
 * Recebe atualização de status de pedido do painel.
 * Muda o status no banco e notifica o WebSocket do restaurante.
 */
import com.qrfood.util.GsonFactory;
import com.qrfood.websocket.PedidoWebSocket;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/pedido/status")
public class PedidoStatusServlet extends HttpServlet {

    private final Gson gson = GsonFactory.create();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("restauranteId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Não autorizado\"}");
            return;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        StatusRequest body = gson.fromJson(sb.toString(), StatusRequest.class);

        if (body == null || body.status == null || body.pedidoId <= 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"pedidoId e status são obrigatórios\"}");
            return;
        }

        try {
            StatusPedido novoStatus = StatusPedido.valueOf(body.status);
            pedidoDAO.atualizarStatus(body.pedidoId, novoStatus);

            // Notifica todos os painéis do restaurante via WebSocket
            String restauranteId = session.getAttribute("restauranteId").toString();
            PedidoWebSocket.notificarRestaurante(restauranteId,
                    new StatusUpdate(body.pedidoId, novoStatus.name()));

            resp.getWriter().write("{\"sucesso\":true}");
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Status inválido: " + body.status + "\"}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── DTOs internos ────────────────────────────────────────────────────────

    private static class StatusRequest {
        long   pedidoId;
        String status;
    }

    /** Mensagem enviada ao WebSocket para diferenciar de um novo pedido */
    public static class StatusUpdate {
        public final String tipo      = "STATUS_UPDATE";
        public final long   pedidoId;
        public final String novoStatus;

        public StatusUpdate(long pedidoId, String novoStatus) {
            this.pedidoId   = pedidoId;
            this.novoStatus = novoStatus;
        }
    }
}