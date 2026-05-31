package com.qrfood.servlet;

import com.google.gson.Gson;
import com.qrfood.dao.ProdutoDAO;
import com.qrfood.model.Produto;
import com.qrfood.util.GsonFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * CRUD de produtos para o painel do restaurante.
 * Todos os métodos exigem sessão autenticada.
 *
 * POST   /admin/produto         → cria produto
 * PUT    /admin/produto         → atualiza produto (requer id no JSON)
 * DELETE /admin/produto?id=X    → remove produto
 */
@WebServlet("/admin/produto")
public class ProdutoAdminServlet extends HttpServlet {

    private final Gson gson = GsonFactory.create();
    private final ProdutoDAO dao = new ProdutoDAO();

    // ── Criação ──────────────────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Long restauranteId = restauranteIdFromSession(req);
        if (restauranteId == null) { unauthorized(resp); return; }

        Produto produto = parseBody(req);
        produto.setRestauranteId(restauranteId);

        if (produto.getNome() == null || produto.getNome().isBlank()
                || produto.getPreco() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"nome e preco são obrigatórios\"}");
            return;
        }

        try {
            long id = dao.inserir(produto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"id\":" + id + "}");
        } catch (Exception e) {
            serverError(resp, e);
        }
    }

    // ── Atualização ──────────────────────────────────────────────────────────

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Long restauranteId = restauranteIdFromSession(req);
        if (restauranteId == null) { unauthorized(resp); return; }

        Produto produto = parseBody(req);
        produto.setRestauranteId(restauranteId); // restringe ao restaurante da sessão

        try {
            dao.atualizar(produto);
            resp.getWriter().write("{\"sucesso\":true}");
        } catch (Exception e) {
            serverError(resp, e);
        }
    }

    // ── Exclusão ─────────────────────────────────────────────────────────────

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");

        Long restauranteId = restauranteIdFromSession(req);
        if (restauranteId == null) { unauthorized(resp); return; }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"id obrigatório\"}");
            return;
        }

        try {
            dao.excluir(Long.parseLong(idParam), restauranteId);
            resp.getWriter().write("{\"sucesso\":true}");
        } catch (Exception e) {
            serverError(resp, e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Long restauranteIdFromSession(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return null;
        Object obj = s.getAttribute("restauranteId");
        return obj instanceof Long ? (Long) obj : null;
    }

    private Produto parseBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return gson.fromJson(sb.toString(), Produto.class);
    }

    private void unauthorized(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write("{\"error\":\"Não autorizado\"}");
    }

    private void serverError(HttpServletResponse resp, Exception e) throws IOException {
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
    }
}