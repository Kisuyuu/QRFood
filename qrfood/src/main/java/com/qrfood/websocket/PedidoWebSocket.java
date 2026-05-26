package com.qrfood.websocket;

import com.google.gson.Gson;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ServerEndpoint(value = "/ws/pedidos/{restauranteId}")
public class PedidoWebSocket {
    // Map restauranteId -> sessions
    private static final Map<String, Set<Session>> SESSIONS = Collections.synchronizedMap(new HashMap<>());
    private static final Gson gson = new Gson();

    @OnOpen
    public void open(Session session, @PathParam("restauranteId") String restauranteId) {
        SESSIONS.computeIfAbsent(restauranteId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    }

    @OnClose
    public void close(Session session, @PathParam("restauranteId") String restauranteId) {
        Set<Session> set = SESSIONS.get(restauranteId);
        if (set != null) set.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // no-op (server pushes only in this app)
    }

    public static void notificarRestaurante(String restauranteId, Object pedidoObj) {
        Set<Session> set = SESSIONS.get(restauranteId);
        if (set == null) return;
        String json = gson.toJson(pedidoObj);
        synchronized (set) {
            for (Session s : set) {
                try {
                    s.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    // ignore or log
                }
            }
        }
    }
}
