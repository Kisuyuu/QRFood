package com.qrfood.websocket;

import com.google.gson.Gson;
import com.qrfood.util.GsonFactory;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Canal WebSocket para atualizações em tempo real dos pedidos do restaurante.
 * Cada restaurante abre uma conexão separada usando seu restauranteId.
 */

@ServerEndpoint("/ws/pedidos/{restauranteId}")
public class PedidoWebSocket {

    // Map restauranteId → conjunto de sessões abertas
    private static final Map<String, Set<Session>> SESSIONS =
            Collections.synchronizedMap(new HashMap<>());

    private static final Gson gson = GsonFactory.create();

    @OnOpen
    public void open(Session session,
                     @PathParam("restauranteId") String restauranteId) {
        SESSIONS
            .computeIfAbsent(restauranteId,
                k -> Collections.synchronizedSet(new HashSet<>()))
            .add(session);
    }

    @OnClose
    public void close(Session session,
                      @PathParam("restauranteId") String restauranteId) {
        Set<Session> set = SESSIONS.get(restauranteId);
        if (set != null) set.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // servidor só envia, não processa mensagens do cliente
    }

    @OnError
    public void onError(Session session, Throwable t) {
        // log opcional
        System.err.println("[WS] Erro na sessão " + session.getId() + ": " + t.getMessage());
    }

    /**
     * Envia objeto serializado como JSON a todas as sessões do restaurante.
     * Sessões fechadas são removidas automaticamente.
     */
    public static void notificarRestaurante(String restauranteId, Object payload) {
        Set<Session> set = SESSIONS.get(restauranteId);
        if (set == null) return;

        String json = gson.toJson(payload);

        synchronized (set) {
            Iterator<Session> it = set.iterator();
            while (it.hasNext()) {
                Session s = it.next();
                if (!s.isOpen()) { it.remove(); continue; }
                try {
                    s.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    it.remove(); // remove sessão com falha
                }
            }
        }
    }
}