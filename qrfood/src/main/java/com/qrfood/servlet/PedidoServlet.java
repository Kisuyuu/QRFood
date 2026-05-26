package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.PedidoDAO;
import com.qrfood.model.Pedido;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/pedido")
public class PedidoServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // espera um JSON com pedido e restauranteId
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        Pedido pedido = gson.fromJson(sb.toString(), Pedido.class);
        String restauranteId = req.getParameter("restauranteId");
        if (restauranteId == null) restauranteId = req.getHeader("X-Restaurante-Id");
        try {
            long id = pedidoDAO.salvarPedido(pedido, restauranteId != null ? restauranteId : "0");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{" + "\"id\":" + id + "}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
