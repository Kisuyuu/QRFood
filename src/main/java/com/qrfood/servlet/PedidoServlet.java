package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.PedidoDAO;
import com.qrfood.dao.RestauranteMesaDAO;

/**
 * Serviço que recebe pedidos vindos do cardápio.
 * Ele valida o JSON, confere se a mesa pertence ao restaurante certo,
 * salva o pedido e retorna o id criado.
 */
import com.qrfood.model.Pedido;
import com.qrfood.util.GsonFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/pedido")
public class PedidoServlet extends HttpServlet {

    private final Gson gson = GsonFactory.create();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }

        Pedido pedido = gson.fromJson(sb.toString(), Pedido.class);
        if (pedido == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"JSON inválido\"}");
            return;
        }

        String restauranteIdParam = req.getParameter("restauranteId");
        if (restauranteIdParam == null || restauranteIdParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"restauranteId obrigatório\"}");
            return;
        }

        long restauranteId;
        try {
            restauranteId = Long.parseLong(restauranteIdParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"restauranteId inválido\"}");
            return;
        }

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Pedido sem itens\"}");
            return;
        }

        try {
            RestauranteMesaDAO mesaDAO = new RestauranteMesaDAO();
            Long idRestauranteDaMesa = mesaDAO.buscarRestauranteIdPorMesa(pedido.getMesaNumero());

            if (idRestauranteDaMesa == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Mesa não encontrada\"}");
                return;
            }

            if (!idRestauranteDaMesa.equals(restauranteId)) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\":\"Esta mesa não pertence a este restaurante\"}");
                return;
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Erro ao validar mesa: " + e.getMessage() + "\"}");
            return;
        }

        pedido.setRestauranteId(restauranteId);

        try {
            long id = pedidoDAO.salvarPedido(pedido);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"id\":" + id + "}");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
