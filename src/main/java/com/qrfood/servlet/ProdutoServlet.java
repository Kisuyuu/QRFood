package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.ProdutoDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Fornece os produtos do restaurante para o cardápio.
 * O front-end pede /produtos?restauranteId=X e recebe JSON de produtos.
 */
@WebServlet("/produtos")
public class ProdutoServlet extends HttpServlet {

    private final ProdutoDAO dao = new ProdutoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(
        HttpServletRequest req,
        HttpServletResponse resp
    ) {

        try {

            long restauranteId =
                Long.parseLong(req.getParameter("restauranteId"));

            String json =
                gson.toJson(
                    dao.listarPorRestaurante(restauranteId)
                );

            resp.setContentType("application/json");
            resp.getWriter().write(json);

        } catch (Exception e) {

            resp.setStatus(500);

        }
    }
}