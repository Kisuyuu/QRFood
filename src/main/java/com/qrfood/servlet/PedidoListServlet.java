package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.PedidoDAO;

/**
 * Retorna a lista de pedidos ativos para o restaurante logado.
 * Exige sessão com restauranteId para proteger o acesso.
 */
import com.qrfood.util.GsonFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/pedidos")
public class PedidoListServlet extends HttpServlet {

    private final Gson gson = GsonFactory.create();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("restauranteId") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Não autorizado\"}");
            return;
        }

        long restauranteId = (Long) session.getAttribute("restauranteId");

        try {
            resp.getWriter().write(gson.toJson(pedidoDAO.listarPorRestaurante(restauranteId)));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}